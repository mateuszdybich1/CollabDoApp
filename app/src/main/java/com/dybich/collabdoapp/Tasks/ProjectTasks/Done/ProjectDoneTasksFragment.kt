package com.dybich.collabdoapp.Tasks.ProjectTasks.Done

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.dybich.collabdoapp.API.KeycloakAPI
import com.dybich.collabdoapp.API.TaskAPI
import com.dybich.collabdoapp.R
import com.dybich.collabdoapp.Snackbar
import com.dybich.collabdoapp.TaskStatus
import com.dybich.collabdoapp.Tasks.ProjectTasks.ProjectTasksAdapter
import com.dybich.collabdoapp.Tasks.TaskViewModel
import com.dybich.collabdoapp.UserViewModel
import com.dybich.collabdoapp.databinding.FragmentProjectTasksBinding
import com.google.android.material.progressindicator.CircularProgressIndicator
import java.time.Instant


class ProjectDoneTasksFragment : Fragment() {


    private var email: String? = null
    private var password : String? = null
    private var refreshToken : String? = null
    private var isLeader:Boolean?=false
    private var projectId:String?=null

    private val userViewModel: UserViewModel by activityViewModels()
    private val taskViewModel: TaskViewModel by activityViewModels()

    private lateinit var snackbar : Snackbar

    private lateinit var infoTV: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var loadMore: CircularProgressIndicator
    private lateinit var loadData: CircularProgressIndicator
    private lateinit var refresh: SwipeRefreshLayout

    private lateinit var binding : FragmentProjectTasksBinding
    private lateinit var keycloakAPI: KeycloakAPI
    private lateinit var taskAPI: TaskAPI

    private lateinit var adapter : ProjectTasksAdapter

    private lateinit var projectDoneTasksViewModel: ProjectDoneTasksViewModel
    private var isLoading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        keycloakAPI = KeycloakAPI()
        taskAPI = TaskAPI()

        projectDoneTasksViewModel = ViewModelProvider(this).get(ProjectDoneTasksViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        email = userViewModel.email.value
        password = userViewModel.password.value
        refreshToken = userViewModel.refreshToken.value
        isLeader = userViewModel.isLeader.value
        projectId = taskViewModel.projectId.value


        binding = FragmentProjectTasksBinding.inflate(inflater, container, false)

        snackbar = com.dybich.collabdoapp.Snackbar(binding.root,binding.root.context)

        infoTV = binding.projectTasksTV
        recyclerView = binding.projectTasksRV
        loadMore = binding.projectTasksLoadMore
        refresh = binding.projectTasksRefresh
        loadData = binding.LoadData



        refresh.setOnRefreshListener {

            isLoading = false
            val now : Instant = Instant.now()
            projectDoneTasksViewModel.miliseconds.value = now.toEpochMilli()
            projectDoneTasksViewModel.pageNumber.value = 1
            performKeycloakAction(projectId!!,projectDoneTasksViewModel.miliseconds.value!!, TaskStatus.Done,projectDoneTasksViewModel.pageNumber.value!!)
        }


        val now : Instant = Instant.now()
        projectDoneTasksViewModel.miliseconds.value = now.toEpochMilli()
        projectDoneTasksViewModel.pageNumber.value = 1
        performKeycloakAction(projectId!!,projectDoneTasksViewModel.miliseconds.value!!, TaskStatus.Done,projectDoneTasksViewModel.pageNumber.value!!)
        loadData.visibility = View.VISIBLE

        return binding.root
    }


    private fun getProjectDoneTasks(accessToken:String, projectId : String, requestDate:Long, taskStatus: TaskStatus, pageNumber : Int){
        taskAPI.getAllTasks(accessToken,projectId,requestDate,taskStatus,pageNumber,
            onSuccess = {list->
                if (isAdded) {
                    loadData.visibility = View.GONE
                    if (list != null) {
                        if (list.isNotEmpty()) {
                            if (projectDoneTasksViewModel.pageNumber.value == 1) {
                                infoTV.visibility = View.GONE
                                projectDoneTasksViewModel.projectsTasks.value = list
                                recyclerView.layoutManager = LinearLayoutManager(binding.root.context)
                                val mainNavController =
                                    Navigation.findNavController(requireActivity(), R.id.drawerFragmentCV)
                                adapter = ProjectTasksAdapter(
                                    projectDoneTasksViewModel.projectsTasks.value!!,
                                    refreshToken!!,
                                    email!!,
                                    password!!,
                                    isLeader!!,
                                    binding.root,
                                    "AllTasks",
                                    mainNavController
                                )
                                recyclerView.adapter = adapter


                                val listener = object : ProjectTasksAdapter.OnItemClickListenerProject {
                                    override fun onItemCLick(position: Int) {
                                        projectDoneTasksViewModel.projectsTasks.value!!.removeAt(position)
                                        adapter.notifyItemRemoved(position)
                                        for (i in position until projectDoneTasksViewModel.projectsTasks.value!!.size) {
                                            adapter.notifyItemChanged(i)
                                        }
                                        if (projectDoneTasksViewModel.projectsTasks.value!!.size == 0) {
                                            infoTV.visibility = View.VISIBLE
                                        }
                                    }

                                }
                                adapter.setOnItemClickListener(listener)


                            } else if (projectDoneTasksViewModel.pageNumber.value!! > 1) {
                                loadMore.visibility = View.GONE
                                projectDoneTasksViewModel.projectsTasks.value?.addAll(list)
                                adapter.notifyDataSetChanged()
                            }
                            if (list.size == 0) {
                                loadMore.visibility = View.GONE
                                recyclerView.clearOnScrollListeners()
                                isLoading = true
                            }
                            if (list.size > 0) {
                                isLoading = false
                            }

                            if (list.size == 5 && refresh.isRefreshing == false) {
                                recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                                        super.onScrolled(recyclerView, dx, dy)

                                        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                                        val visibleItemCount = layoutManager.childCount
                                        val totalItemCount = layoutManager.itemCount
                                        val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()



                                        if (!isLoading && dy > 0 && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount && projectDoneTasksViewModel.projectsTasks.value!!.size % 5 == 0) {
                                            isLoading = true
                                            loadMore.visibility = View.VISIBLE
                                            projectDoneTasksViewModel.pageNumber.value =
                                                projectDoneTasksViewModel.pageNumber.value!! + 1
                                            performKeycloakAction(
                                                projectId!!, projectDoneTasksViewModel.miliseconds.value!!,
                                                TaskStatus.Done, projectDoneTasksViewModel.pageNumber.value!!
                                            )
                                        }
                                    }
                                })
                            }

                        } else {
                            if (projectDoneTasksViewModel.pageNumber.value!! > 1 && list.size == 0) {
                                snackbar.show("No new items")

                            } else {
                                infoTV.visibility = View.VISIBLE
                            }

                            loadMore.visibility = View.GONE

                        }
                    }
                    refresh.isRefreshing = false
                }
            },
            onFailure = {error->
                snackbar.show(error)
                infoTV.visibility = View.VISIBLE
                refresh.isRefreshing = false
                loadMore.visibility = View.GONE
                loadData.visibility = View.GONE
            })
    }


    private fun performKeycloakAction(projectId : String, requestDate:Long, taskStatus: TaskStatus, pageNumber : Int) {
        keycloakAPI.getFromRefreshToken(refreshToken!!,
            onSuccess = { data ->
                refreshToken = data.refresh_token

                getProjectDoneTasks(data.access_token,projectId,requestDate,taskStatus,pageNumber)

            },
            onFailure = { error ->
                if (error == "Refresh token expired" || error == "Token is not active") {
                    keycloakAPI.getFromEmailAndPass(email!!, password!!,
                        onSuccess = { data ->
                            refreshToken = data.refresh_token
                            getProjectDoneTasks(data.access_token,projectId,requestDate,taskStatus,pageNumber)
                        },
                        onFailure = { err ->
                            snackbar.show(err)
                            infoTV.visibility = View.VISIBLE
                            refresh.isRefreshing = false
                            loadMore.visibility = View.GONE
                            loadData.visibility = View.GONE
                        })
                } else {
                    snackbar.show(error)
                    infoTV.visibility = View.VISIBLE
                    refresh.isRefreshing = false
                    loadMore.visibility = View.GONE
                    loadData.visibility = View.GONE
                }

            })
    }



}