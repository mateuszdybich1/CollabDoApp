package com.dybich.collabdoapp.Projects

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.dybich.collabdoapp.API.KeycloakAPI
import com.dybich.collabdoapp.API.ProjectAPI
import com.dybich.collabdoapp.ProjectStatus
import com.dybich.collabdoapp.R
import com.dybich.collabdoapp.UserViewModel
import com.dybich.collabdoapp.Snackbar
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import java.time.Instant


class ProjectsFragment : Fragment() {

    private var email: String? = null
    private var password : String? = null
    private var refreshToken : String? = null
    private var isLeader : Boolean? = false
    private var leaderId : String? = null


    private var isLoading = false
    private lateinit var infoTV : TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var refresh: SwipeRefreshLayout
    private lateinit var loadMore :CircularProgressIndicator
    private lateinit var loadProjects : CircularProgressIndicator

    private val userViewModel: UserViewModel by activityViewModels()

    private lateinit var snackbar : Snackbar

    private lateinit var keycloakAPI : KeycloakAPI
    private lateinit var projectAPI : ProjectAPI

    private lateinit var adapter: ProjectsAdapter

    private lateinit var projectsViewModel: ProjectsViewModel

    private lateinit var view: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        keycloakAPI = KeycloakAPI()
        projectAPI = ProjectAPI()

        projectsViewModel = ViewModelProvider(this).get(ProjectsViewModel::class.java)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        email = userViewModel.email.value
        password = userViewModel.password.value
        refreshToken = userViewModel.refreshToken.value
        isLeader = userViewModel.isLeader.value
        leaderId = userViewModel.leaderId.value



        view =  inflater.inflate(R.layout.fragment_projects, container, false)

        snackbar = com.dybich.collabdoapp.Snackbar(view,view.context)

        val addProject : ExtendedFloatingActionButton = view.findViewById(R.id.addProjectBTN)
        refresh = view.findViewById(R.id.projectRefresh)
        recyclerView = view.findViewById(R.id.projectRV)
        infoTV = view.findViewById(R.id.projectsTV)
        loadMore = view.findViewById(R.id.projectsLoadMore)
        loadProjects = view.findViewById(R.id.loadProjects)


        if(!isLeader!!){
            addProject.visibility = View.GONE
        }
        else{
            addProject.setOnClickListener {
                val navController = findNavController()
                navController.navigate(R.id.addProjectFragment)
                view.visibility = View.GONE
            }
        }

        if (projectsViewModel.isSaved.value == true) {

            if(projectsViewModel.projectList.value!= null && projectsViewModel.projectList.value!!.isNotEmpty()){
                infoTV.visibility = View.GONE


                recyclerView.layoutManager = LinearLayoutManager(view.context)
                adapter = ProjectsAdapter(projectsViewModel.projectList.value!!,refreshToken!!,email!!,password!!,isLeader!!, view)


                recyclerView.adapter = adapter



                val listener = object : ProjectsAdapter.OnItemClickListenerProject {
                    override fun onItemCLick(position: Int) {
                        projectsViewModel.projectList.value!!.removeAt(position)
                        adapter.notifyItemRemoved(position)
                        for (i in position until projectsViewModel.projectList.value!!.size) {
                            adapter.notifyItemChanged(i)
                        }
                        if(projectsViewModel.projectList.value!!.size == 0){
                            infoTV.visibility = View.VISIBLE
                        }
                    }

                }
                adapter.setOnItemClickListener(listener)

                if(projectsViewModel.projectList.value!!.size % 10 == 0){
                    recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                            super.onScrolled(recyclerView, dx, dy)

                            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                            val visibleItemCount = layoutManager.childCount
                            val totalItemCount = layoutManager.itemCount
                            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()


                            if (!isLoading && dy > 0 && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount && projectsViewModel.projectList.value!!.size % 10==0) {

                                isLoading = true
                                loadMore.visibility = View.VISIBLE
                                projectsViewModel.pageNumber.value = projectsViewModel.pageNumber.value!!+1
                                performKeycloakAction(projectsViewModel.miliseconds.value!!, leaderId, projectsViewModel.pageNumber.value!!, ProjectStatus.InProgress)
                            }
                        }
                    })
                }


            }
            else{
                infoTV.visibility = View.VISIBLE
            }

        }
        else if(projectsViewModel.isSaved.value == false || projectsViewModel.isSaved.value == null){
            loadProjects.visibility = View.VISIBLE
            val now : Instant = Instant.now()
            projectsViewModel.miliseconds.value = now.toEpochMilli()
            projectsViewModel.pageNumber.value = 1
            performKeycloakAction(projectsViewModel.miliseconds.value!!,leaderId,projectsViewModel.pageNumber.value!!,ProjectStatus.InProgress)
        }

        refresh.setOnRefreshListener {
            val now : Instant = Instant.now()
            projectsViewModel.miliseconds.value = now.toEpochMilli()
            projectsViewModel.pageNumber.value = 1
            performKeycloakAction(projectsViewModel.miliseconds.value!!,leaderId,projectsViewModel.pageNumber.value!!,ProjectStatus.InProgress)
        }

        return view
    }

    private fun getProjects(accessToken:String,requestDate:Long, leaderId : String?, pageNumber : Int?,projectStatus : ProjectStatus){
        projectAPI.getProjects(
            accessToken,
            requestDate,
            leaderId,
            projectStatus,
            pageNumber,
            onSuccess = {list->
                loadProjects.visibility = View.GONE
                projectsViewModel.isSaved.value = true
                if (list != null) {
                    if(list.isNotEmpty()){
                        if(projectsViewModel.pageNumber.value == 1){
                            infoTV.visibility = View.GONE
                            projectsViewModel.projectList.value = list
                            recyclerView.layoutManager = LinearLayoutManager(view.context)
                            adapter = ProjectsAdapter(projectsViewModel.projectList.value!!,refreshToken!!,email!!,password!!,isLeader!!, view)
                            recyclerView.adapter = adapter




                            val listener = object : ProjectsAdapter.OnItemClickListenerProject {
                                override fun onItemCLick(position: Int) {
                                    projectsViewModel.projectList.value!!.removeAt(position)
                                    adapter.notifyItemRemoved(position)
                                    for (i in position until projectsViewModel.projectList.value!!.size) {
                                        adapter.notifyItemChanged(i)
                                    }
                                    if(projectsViewModel.projectList.value!!.size == 0){
                                        infoTV.visibility = View.VISIBLE
                                    }
                                }

                            }
                            adapter.setOnItemClickListener(listener)


                        }
                        else if(projectsViewModel.pageNumber.value!! >1){
                            loadMore.visibility = View.GONE
                            projectsViewModel.projectList.value?.addAll(list)
                            adapter.notifyDataSetChanged()
                        }
                        if (list.size == 0) {
                            loadMore.visibility = View.GONE
                            recyclerView.clearOnScrollListeners()
                        }
                        if(list.size>0){
                            isLoading = false
                        }

                        if(list.size == 10 && refresh.isRefreshing == false){
                            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                                    super.onScrolled(recyclerView, dx, dy)

                                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                                    val visibleItemCount = layoutManager.childCount
                                    val totalItemCount = layoutManager.itemCount
                                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()



                                    if (!isLoading && dy > 0 && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount && projectsViewModel.projectList.value!!.size % 10 == 0) {
                                        isLoading = true
                                        loadMore.visibility = View.VISIBLE
                                        projectsViewModel.pageNumber.value = projectsViewModel.pageNumber.value!! +1
                                        performKeycloakAction(projectsViewModel.miliseconds.value!!, leaderId, projectsViewModel.pageNumber.value!!, ProjectStatus.InProgress)
                                    }
                                }
                            })
                        }



                    }
                    else{
                        if(projectsViewModel.pageNumber.value!!>1 && list.size == 0){
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
                else{
                    snackbar.show("ERROR")
                    if(projectsViewModel.projectList.value!!.size == 0){
                        infoTV.visibility = View.VISIBLE
                    }
                    refresh.isRefreshing = false
                    loadMore.visibility = View.GONE
                    isLoading = false
                }
                refresh.isRefreshing = false

            }, onFailure ={error->
                refresh.isRefreshing = false
                if(projectsViewModel.projectList.value!!.size == 0){
                    infoTV.visibility = View.VISIBLE
                }

                snackbar.show(error)
                loadMore.visibility = View.GONE
                isLoading = false
                loadProjects.visibility = View.GONE
            })
    }

    private fun performKeycloakAction(requestDate:Long, leaderId : String?, pageNumber : Int?,projectStatus : ProjectStatus){
        keycloakAPI.getFromRefreshToken(refreshToken!!,
            onSuccess = {data ->
                refreshToken = data.refresh_token
                getProjects(data.access_token,requestDate,leaderId,pageNumber,projectStatus)

            },
            onFailure = {error->
                if(error == "Refresh token expired" || error=="Token is not active"){
                    keycloakAPI.getFromEmailAndPass(email!!,password!!,
                        onSuccess = {data ->
                            refreshToken = data.refresh_token
                            getProjects(data.access_token,requestDate,leaderId,pageNumber,projectStatus)
                        },
                        onFailure = {err->
                            snackbar.show(err)
                            if(projectsViewModel.projectList.value!!.size == 0){
                                infoTV.visibility = View.VISIBLE
                            }
                            refresh.isRefreshing = false
                            loadMore.visibility = View.GONE
                            isLoading = false
                            loadProjects.visibility = View.GONE
                        })
                }
                else{
                    snackbar.show(error)
                    if(projectsViewModel.projectList.value!!.size == 0){
                        infoTV.visibility = View.VISIBLE
                    }
                    refresh.isRefreshing = false
                    loadMore.visibility = View.GONE
                    isLoading = false
                    loadProjects.visibility = View.GONE
                }

            })
    }

}