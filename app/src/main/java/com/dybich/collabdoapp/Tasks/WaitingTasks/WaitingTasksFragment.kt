package com.dybich.collabdoapp.Tasks.WaitingTasks

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
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


class WaitingTasksFragment : Fragment() {
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

    private lateinit var waitingTasksViewModel: WaitingTasksViewModel
    private var isLoading = false
    private lateinit var mainNavController:NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        keycloakAPI = KeycloakAPI()
        taskAPI = TaskAPI()
        mainNavController = Navigation.findNavController(requireActivity(), R.id.drawerFragmentCV)

        waitingTasksViewModel = ViewModelProvider(this).get(WaitingTasksViewModel::class.java)
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

            waitingTasksViewModel.isLoading.value = false
            val now : Instant = Instant.now()
            waitingTasksViewModel.miliseconds.value = now.toEpochMilli()
            waitingTasksViewModel.pageNumber.value = 1
            performKeycloakAction(projectId!!,waitingTasksViewModel.miliseconds.value!!, TaskStatus.WaitingForApprovement,waitingTasksViewModel.pageNumber.value!!,isLeader!!)
        }

        if(waitingTasksViewModel.isSaved.value == true){

            if(waitingTasksViewModel.myTasks.value!= null && waitingTasksViewModel.myTasks.value!!.isNotEmpty()){
                infoTV.visibility = View.GONE


                recyclerView.layoutManager = LinearLayoutManager(binding.root.context)
                adapter = ProjectTasksAdapter(waitingTasksViewModel.myTasks.value!!,refreshToken!!,email!!,password!!,isLeader!!, binding.root,"WaitingTasks",mainNavController)


                recyclerView.adapter = adapter



                val listener = object : ProjectTasksAdapter.OnItemClickListenerProject {
                    override fun onItemCLick(position: Int) {
                        waitingTasksViewModel.myTasks.value!!.removeAt(position)
                        adapter.notifyItemRemoved(position)
                        for (i in position until waitingTasksViewModel.myTasks.value!!.size) {
                            adapter.notifyItemChanged(i)
                        }
                        if(waitingTasksViewModel.myTasks.value!!.size == 0){
                            infoTV.visibility = View.VISIBLE
                        }
                    }

                }
                adapter.setOnItemClickListener(listener)

                if (waitingTasksViewModel.myTasks.value!!.size == 0) {
                    loadMore.visibility = View.GONE
                    recyclerView.clearOnScrollListeners()
                    waitingTasksViewModel.isLoading.value = true
                }
                if(waitingTasksViewModel.myTasks.value!!.size>0){
                    waitingTasksViewModel.isLoading.value = false
                }

                if(waitingTasksViewModel.myTasks.value!!.size %5 ==0){
                    recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                            super.onScrolled(recyclerView, dx, dy)

                            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                            val visibleItemCount = layoutManager.childCount
                            val totalItemCount = layoutManager.itemCount
                            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()



                            if (waitingTasksViewModel.isLoading.value == false && dy > 0 && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount && waitingTasksViewModel.myTasks.value!!.size % 5==0) {
                                waitingTasksViewModel.isLoading.value = true
                                loadMore.visibility = View.VISIBLE
                                waitingTasksViewModel.pageNumber.value = waitingTasksViewModel.pageNumber.value!! +1
                                performKeycloakAction(projectId!!,waitingTasksViewModel.miliseconds.value!!,
                                    TaskStatus.WaitingForApprovement,waitingTasksViewModel.pageNumber.value!!, isLeader!!)
                            }
                        }
                    })
                }


            }
            else{
                infoTV.visibility = View.VISIBLE
            }


        }
        else if(waitingTasksViewModel.isSaved.value == false || waitingTasksViewModel.isSaved.value == null){
            waitingTasksViewModel.isLoading.value = false
            val now : Instant = Instant.now()
            waitingTasksViewModel.miliseconds.value = now.toEpochMilli()
            waitingTasksViewModel.pageNumber.value = 1
            performKeycloakAction(projectId!!,waitingTasksViewModel.miliseconds.value!!, TaskStatus.WaitingForApprovement,waitingTasksViewModel.pageNumber.value!!, isLeader!!)
            loadData.visibility = View.VISIBLE

        }

        return binding.root
    }

    private fun getAllWaitingTasks(accessToken:String, projectId : String, requestDate:Long, taskStatus: TaskStatus, pageNumber : Int){
        taskAPI.getAllTasks(accessToken,projectId,requestDate,taskStatus,pageNumber,
            onSuccess = {list->
                waitingTasksViewModel.isSaved.value = true
                loadData.visibility = View.GONE
                if (list != null) {
                    if(list.isNotEmpty()){
                        if(waitingTasksViewModel.pageNumber.value == 1){
                            infoTV.visibility = View.GONE
                            waitingTasksViewModel.myTasks.value = list
                            recyclerView.layoutManager = LinearLayoutManager(binding.root.context)
                            adapter = ProjectTasksAdapter(waitingTasksViewModel.myTasks.value!!,refreshToken!!,email!!,password!!,isLeader!!, binding.root,"WaitingTasks",mainNavController)
                            recyclerView.adapter = adapter


                            val listener = object : ProjectTasksAdapter.OnItemClickListenerProject {
                                override fun onItemCLick(position: Int) {
                                    waitingTasksViewModel.myTasks.value!!.removeAt(position)
                                    adapter.notifyItemRemoved(position)
                                    for (i in position until waitingTasksViewModel.myTasks.value!!.size) {
                                        adapter.notifyItemChanged(i)
                                    }
                                    if(waitingTasksViewModel.myTasks.value!!.size == 0){
                                        infoTV.visibility = View.VISIBLE
                                    }
                                }

                            }
                            adapter.setOnItemClickListener(listener)




                        }
                        else if(waitingTasksViewModel.pageNumber.value!! >1){
                            loadMore.visibility = View.GONE
                            waitingTasksViewModel.myTasks.value?.addAll(list)
                            adapter.notifyDataSetChanged()
                        }
                        if (list.size == 0) {
                            loadMore.visibility = View.GONE
                            recyclerView.clearOnScrollListeners()
                            waitingTasksViewModel.isLoading.value = true
                        }
                        if(list.size>0){
                            waitingTasksViewModel.isLoading.value = false
                        }

                        if(list.size == 5){
                            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                                    super.onScrolled(recyclerView, dx, dy)

                                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                                    val visibleItemCount = layoutManager.childCount
                                    val totalItemCount = layoutManager.itemCount
                                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()



                                    if (waitingTasksViewModel.isLoading.value == false && dy > 0 && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount && waitingTasksViewModel.myTasks.value!!.size % 5==0) {
                                        waitingTasksViewModel.isLoading.value = true
                                        loadMore.visibility = View.VISIBLE
                                        waitingTasksViewModel.pageNumber.value = waitingTasksViewModel.pageNumber.value!! +1
                                        performKeycloakAction(projectId!!,waitingTasksViewModel.miliseconds.value!!,
                                            TaskStatus.WaitingForApprovement,waitingTasksViewModel.pageNumber.value!!,isLeader!!)
                                    }
                                }
                            })
                        }

                    }
                    else{
                        if(waitingTasksViewModel.pageNumber.value!!>1 && list.size == 0){
                            snackbar.show("No new items")

                        }
                        else{
                            if(!isLeader!!){
                                infoTV.text = "No projects found."

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

    private fun getMyWaitingTasks(accessToken:String, projectId : String, requestDate:Long, taskStatus: TaskStatus, pageNumber : Int){
        taskAPI.getUserTasks(accessToken,projectId,requestDate,taskStatus,pageNumber,
            onSuccess = {list->
                waitingTasksViewModel.isSaved.value = true
                loadData.visibility = View.GONE
                if (list != null) {
                    if(list.isNotEmpty()){
                        if(waitingTasksViewModel.pageNumber.value == 1){
                            infoTV.visibility = View.GONE
                            waitingTasksViewModel.myTasks.value = list
                            recyclerView.layoutManager = LinearLayoutManager(binding.root.context)
                            adapter = ProjectTasksAdapter(waitingTasksViewModel.myTasks.value!!,refreshToken!!,email!!,password!!,isLeader!!, binding.root,"WaitingTasks",mainNavController)
                            recyclerView.adapter = adapter


                            val listener = object : ProjectTasksAdapter.OnItemClickListenerProject {
                                override fun onItemCLick(position: Int) {
                                    waitingTasksViewModel.myTasks.value!!.removeAt(position)
                                    adapter.notifyItemRemoved(position)
                                    for (i in position until waitingTasksViewModel.myTasks.value!!.size) {
                                        adapter.notifyItemChanged(i)
                                    }
                                    if(waitingTasksViewModel.myTasks.value!!.size == 0){
                                        infoTV.visibility = View.VISIBLE
                                    }
                                }

                            }
                            adapter.setOnItemClickListener(listener)




                        }
                        else if(waitingTasksViewModel.pageNumber.value!! >1){
                            loadMore.visibility = View.GONE
                            waitingTasksViewModel.myTasks.value?.addAll(list)
                            adapter.notifyDataSetChanged()
                        }
                        if (list.size == 0) {
                            loadMore.visibility = View.GONE
                            recyclerView.clearOnScrollListeners()
                            waitingTasksViewModel.isLoading.value = true
                        }
                        if(list.size>0){
                            waitingTasksViewModel.isLoading.value = false
                        }

                        if(list.size == 5){
                            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                                    super.onScrolled(recyclerView, dx, dy)

                                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                                    val visibleItemCount = layoutManager.childCount
                                    val totalItemCount = layoutManager.itemCount
                                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()



                                    if (waitingTasksViewModel.isLoading.value == false && dy > 0 && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount && waitingTasksViewModel.myTasks.value!!.size % 5==0) {
                                        waitingTasksViewModel.isLoading.value = true
                                        loadMore.visibility = View.VISIBLE
                                        waitingTasksViewModel.pageNumber.value = waitingTasksViewModel.pageNumber.value!! +1
                                        performKeycloakAction(projectId!!,waitingTasksViewModel.miliseconds.value!!,
                                            TaskStatus.WaitingForApprovement,waitingTasksViewModel.pageNumber.value!!,isLeader!!)
                                    }
                                }
                            })
                        }

                    }
                    else{
                        if(waitingTasksViewModel.pageNumber.value!!>1 && list.size == 0){
                            snackbar.show("No new items")

                        }
                        else{
                            if(!isLeader!!){
                                infoTV.text = "You don't have waiting for approvement tasks."

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

    private fun performKeycloakAction(projectId : String, requestDate:Long, taskStatus: TaskStatus, pageNumber : Int, isLeader:Boolean) {
        keycloakAPI.getFromRefreshToken(refreshToken!!,
            onSuccess = { data ->
                refreshToken = data.refresh_token

                if(isLeader==true){
                    getAllWaitingTasks(data.access_token,projectId,requestDate,taskStatus,pageNumber)
                }
                else if(isLeader==false){
                    getMyWaitingTasks(data.access_token,projectId,requestDate,taskStatus,pageNumber)
                }


            },
            onFailure = { error ->
                if (error == "Refresh token expired" || error == "Token is not active") {
                    keycloakAPI.getFromEmailAndPass(email!!, password!!,
                        onSuccess = { data ->
                            refreshToken = data.refresh_token
                            getMyWaitingTasks(data.access_token,projectId,requestDate,taskStatus,pageNumber)
                        },
                        onFailure = { err ->
                            snackbar.show(err)
                            if(waitingTasksViewModel.myTasks == null || waitingTasksViewModel.myTasks.value!!.size == 0){
                                infoTV.visibility = View.VISIBLE
                            }
                            refresh.isRefreshing = false
                            loadMore.visibility = View.GONE
                            loadData.visibility = View.GONE
                        })
                } else {
                    snackbar.show(error)
                    if(waitingTasksViewModel.myTasks == null || waitingTasksViewModel.myTasks.value!!.size == 0){
                        infoTV.visibility = View.VISIBLE
                    }

                    refresh.isRefreshing = false
                    loadMore.visibility = View.GONE
                    loadData.visibility = View.GONE
                }

            })
    }


}