package com.dybich.collabdoapp.Tasks.MyTasks

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.dybich.collabdoapp.*
import com.dybich.collabdoapp.API.KeycloakAPI
import com.dybich.collabdoapp.API.TaskAPI
import com.dybich.collabdoapp.Projects.ProjectsAdapter
import com.dybich.collabdoapp.Tasks.MyAllTasks.DoneTasks.MyDoneTasksFragment
import com.dybich.collabdoapp.Tasks.MyAllTasks.UndoneTasks.MyUndoneTasksFragment
import com.dybich.collabdoapp.Tasks.MyAllTasks.UndoneTasks.MyUndoneTasksViewModel
import com.dybich.collabdoapp.Tasks.ProjectTasks.Done.ProjectDoneTasksFragment
import com.dybich.collabdoapp.Tasks.ProjectTasks.PendingTasks.ProjectsPendingTasksFragment
import com.dybich.collabdoapp.Tasks.ProjectTasks.ProjectTasksAdapter
import com.dybich.collabdoapp.Tasks.ProjectTasks.ProjectsTaskFragment
import com.dybich.collabdoapp.Tasks.ProjectTasks.StartedTasks.ProjectStartedTasksFragment
import com.dybich.collabdoapp.Tasks.ProjectTasks.Undone.ProjectsUndoneTasksFragment
import com.dybich.collabdoapp.Tasks.TaskViewModel
import com.dybich.collabdoapp.databinding.FragmentMyTasksBinding
import com.dybich.collabdoapp.databinding.FragmentProjectTasksBinding
import com.dybich.collabdoapp.databinding.FragmentProjectTasksMainBinding
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import java.time.Instant


class MyTasksFragment : Fragment() {

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

    private lateinit var myTasksViewModel: MyTasksViewModel
    private var isLoading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        keycloakAPI = KeycloakAPI()
        taskAPI = TaskAPI()

        myTasksViewModel = ViewModelProvider(this).get(MyTasksViewModel::class.java)
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

            myTasksViewModel.isLoading.value = false
            val now : Instant = Instant.now()
            myTasksViewModel.miliseconds.value = now.toEpochMilli()
            myTasksViewModel.pageNumber.value = 1
            performKeycloakAction(projectId!!,myTasksViewModel.miliseconds.value!!, TaskStatus.Started,myTasksViewModel.pageNumber.value!!)
        }

        if(myTasksViewModel.isSaved.value == true){

            if(myTasksViewModel.myTasks.value!= null && myTasksViewModel.myTasks.value!!.isNotEmpty()){
                infoTV.visibility = View.GONE


                recyclerView.layoutManager = LinearLayoutManager(binding.root.context)
                val mainNavController = Navigation.findNavController(requireActivity(), R.id.drawerFragmentCV)
                adapter = ProjectTasksAdapter(myTasksViewModel.myTasks.value!!,refreshToken!!,email!!,password!!,isLeader!!, binding.root,"MyTasks",mainNavController)


                recyclerView.adapter = adapter



                val listener = object : ProjectTasksAdapter.OnItemClickListenerProject {
                    override fun onItemCLick(position: Int) {
                        myTasksViewModel.myTasks.value!!.removeAt(position)
                        adapter.notifyItemRemoved(position)
                        for (i in position until myTasksViewModel.myTasks.value!!.size) {
                            adapter.notifyItemChanged(i)
                        }
                        if(myTasksViewModel.myTasks.value!!.size == 0){
                            infoTV.visibility = View.VISIBLE
                        }
                    }

                }
                adapter.setOnItemClickListener(listener)

                if (myTasksViewModel.myTasks.value!!.size == 0) {
                    loadMore.visibility = View.GONE
                    recyclerView.clearOnScrollListeners()
                    myTasksViewModel.isLoading.value = true
                }
                if(myTasksViewModel.myTasks.value!!.size>0){
                    myTasksViewModel.isLoading.value = false
                }

                if(myTasksViewModel.myTasks.value!!.size %5 ==0){
                    recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                            super.onScrolled(recyclerView, dx, dy)

                            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                            val visibleItemCount = layoutManager.childCount
                            val totalItemCount = layoutManager.itemCount
                            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()



                            if (myTasksViewModel.isLoading.value == false && dy > 0 && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount && myTasksViewModel.myTasks.value!!.size % 5==0) {
                                myTasksViewModel.isLoading.value = true
                                loadMore.visibility = View.VISIBLE
                                myTasksViewModel.pageNumber.value = myTasksViewModel.pageNumber.value!! +1
                                performKeycloakAction(projectId!!,myTasksViewModel.miliseconds.value!!,
                                    TaskStatus.Started,myTasksViewModel.pageNumber.value!!)
                            }
                        }
                    })
                }


            }
            else{
                infoTV.visibility = View.VISIBLE
            }


        }
        else if(myTasksViewModel.isSaved.value == false || myTasksViewModel.isSaved.value == null){
            myTasksViewModel.isLoading.value = false
            val now : Instant = Instant.now()
            myTasksViewModel.miliseconds.value = now.toEpochMilli()
            myTasksViewModel.pageNumber.value = 1
            performKeycloakAction(projectId!!,myTasksViewModel.miliseconds.value!!, TaskStatus.Started,myTasksViewModel.pageNumber.value!!)
            loadData.visibility = View.VISIBLE

        }



        return binding.root
    }

    private fun getMyStartedTasks(accessToken:String, projectId : String, requestDate:Long, taskStatus: TaskStatus, pageNumber : Int){
        taskAPI.getUserTasks(accessToken,projectId,requestDate,taskStatus,pageNumber,
            onSuccess = {list->
                myTasksViewModel.isSaved.value = true
                loadData.visibility = View.GONE
                if (list != null) {
                    if(list.isNotEmpty()){
                        if(myTasksViewModel.pageNumber.value == 1){
                            infoTV.visibility = View.GONE
                            myTasksViewModel.myTasks.value = list
                            recyclerView.layoutManager = LinearLayoutManager(binding.root.context)
                            val mainNavController = Navigation.findNavController(requireActivity(), R.id.drawerFragmentCV)
                            adapter = ProjectTasksAdapter(myTasksViewModel.myTasks.value!!,refreshToken!!,email!!,password!!,isLeader!!, binding.root,"MyTasks",mainNavController)
                            recyclerView.adapter = adapter


                            val listener = object : ProjectTasksAdapter.OnItemClickListenerProject {
                                override fun onItemCLick(position: Int) {
                                    myTasksViewModel.myTasks.value!!.removeAt(position)
                                    adapter.notifyItemRemoved(position)
                                    for (i in position until myTasksViewModel.myTasks.value!!.size) {
                                        adapter.notifyItemChanged(i)
                                    }
                                    if(myTasksViewModel.myTasks.value!!.size == 0){
                                        infoTV.visibility = View.VISIBLE
                                    }
                                }

                            }
                            adapter.setOnItemClickListener(listener)




                        }
                        else if(myTasksViewModel.pageNumber.value!! >1){
                            loadMore.visibility = View.GONE
                            myTasksViewModel.myTasks.value?.addAll(list)
                            adapter.notifyDataSetChanged()
                        }
                        if (list.size == 0) {
                            loadMore.visibility = View.GONE
                            recyclerView.clearOnScrollListeners()
                            myTasksViewModel.isLoading.value = true
                        }
                        if(list.size>0){
                            myTasksViewModel.isLoading.value = false
                        }

                        if(list.size == 5){
                            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                                    super.onScrolled(recyclerView, dx, dy)

                                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                                    val visibleItemCount = layoutManager.childCount
                                    val totalItemCount = layoutManager.itemCount
                                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()



                                    if (myTasksViewModel.isLoading.value == false && dy > 0 && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount && myTasksViewModel.myTasks.value!!.size % 5==0) {
                                        myTasksViewModel.isLoading.value = true
                                        loadMore.visibility = View.VISIBLE
                                        myTasksViewModel.pageNumber.value = myTasksViewModel.pageNumber.value!! +1
                                        performKeycloakAction(projectId!!,myTasksViewModel.miliseconds.value!!,
                                            TaskStatus.Started,myTasksViewModel.pageNumber.value!!)
                                    }
                                }
                            })
                        }

                    }
                    else{
                        if(myTasksViewModel.pageNumber.value!!>1 && list.size == 0){
                            snackbar.show("No new items")

                        }
                        else{
                            if(!isLeader!!){
                                infoTV.text = "You don't have started tasks."

                            }
                            infoTV.visibility = View.VISIBLE
                        }

                        loadMore.visibility = View.GONE

                    }
                }
                refresh.isRefreshing = false
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

                getMyStartedTasks(data.access_token,projectId,requestDate,taskStatus,pageNumber)

            },
            onFailure = { error ->
                if (error == "Refresh token expired" || error == "Token is not active") {
                    keycloakAPI.getFromEmailAndPass(email!!, password!!,
                        onSuccess = { data ->
                            refreshToken = data.refresh_token
                            getMyStartedTasks(data.access_token,projectId,requestDate,taskStatus,pageNumber)
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