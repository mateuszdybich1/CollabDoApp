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
import com.dybich.collabdoapp.Dtos.ProjectDto
import com.dybich.collabdoapp.ProjectStatus
import com.dybich.collabdoapp.R
import com.dybich.collabdoapp.UserViewModel
import com.dybich.collabdoapp.Snackbar
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import java.time.Instant


class ProjectsFragment : Fragment() {

    private var email: String? = null
    private var password : String? = null
    private var refreshToken : String? = null
    private var isLeader : Boolean? = false
    private var leaderId : String? = null


    private lateinit var infoTV : TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var refresh: SwipeRefreshLayout

    private val userViewModel: UserViewModel by activityViewModels()

    private lateinit var snackbar : Snackbar

    private lateinit var keycloakAPI : KeycloakAPI
    private lateinit var projectAPI : ProjectAPI

    private lateinit var adapter: ProjectsAdapter

    private lateinit var projects : ArrayList<ProjectDto>
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

        if (projectsViewModel.isSaved) {

            if(projectsViewModel.projectList!!.isNotEmpty()){
                infoTV.visibility = View.GONE
                projects = projectsViewModel.projectList!!

                recyclerView.layoutManager = LinearLayoutManager(view.context)
                adapter = ProjectsAdapter(projects,refreshToken!!,email!!,password!!, view)
                recyclerView.adapter = adapter



                val listener = object : ProjectsAdapter.OnItemCLickListener {
                    override fun onItemCLick(position: Int) {
                        projects.removeAt(position)
                        adapter.notifyItemRemoved(position)
                        for (i in position until projects.size) {
                            adapter.notifyItemChanged(i)
                        }
                        if(projects.size == 0){
                            infoTV.visibility = View.VISIBLE
                        }
                    }

                }
                adapter.setOnItemCLickListener(listener)
            }
            else{
                infoTV.visibility = View.VISIBLE
            }

        }


        refresh.setOnRefreshListener {
            val now : Instant = Instant.now()
            val miliseconds : Long = now.toEpochMilli()
            performKeycloakAction(miliseconds,leaderId,1,ProjectStatus.InProgress)
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
                refresh.isRefreshing = false

                if (list != null) {
                    if(list.isNotEmpty()){
                        infoTV.visibility = View.GONE
                        projects = list
                        recyclerView.layoutManager = LinearLayoutManager(view.context)
                        adapter = ProjectsAdapter(projects,refreshToken!!,email!!,password!!, view)
                        recyclerView.adapter = adapter



                        val listener = object : ProjectsAdapter.OnItemCLickListener {
                            override fun onItemCLick(position: Int) {
                                projects.removeAt(position)
                                adapter.notifyItemRemoved(position)
                                for (i in position until projects.size) {
                                    adapter.notifyItemChanged(i)
                                }
                                if(projects.size == 0){
                                    infoTV.visibility = View.VISIBLE
                                }
                            }

                        }
                        adapter.setOnItemCLickListener(listener)

                        projectsViewModel.isSaved = true
                        projectsViewModel.projectList = projects


                    }
                    else{
                        if(!isLeader!!){
                            infoTV.text = "No projects found."
                        }
                        infoTV.visibility = View.VISIBLE
                    }
                }
                else{
                    snackbar.show("ERROR")
                    infoTV.visibility = View.VISIBLE
                    refresh.isRefreshing = false
                }

            }, onFailure ={error->
                refresh.isRefreshing = false
                snackbar.show(error)
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



                        },
                        onFailure = {err->
                            snackbar.show(err)
                            infoTV.visibility = View.VISIBLE
                            refresh.isRefreshing = false
                        })
                }
                else{
                    snackbar.show(error)
                    infoTV.visibility = View.VISIBLE
                    refresh.isRefreshing = false
                }

            })
    }

}