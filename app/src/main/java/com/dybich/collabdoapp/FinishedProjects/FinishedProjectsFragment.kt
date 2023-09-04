package com.dybich.collabdoapp.FinishedProjects

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.dybich.collabdoapp.*
import com.dybich.collabdoapp.API.KeycloakAPI
import com.dybich.collabdoapp.API.ProjectAPI
import com.dybich.collabdoapp.Dtos.ProjectDto
import com.dybich.collabdoapp.databinding.FragmentFinishedProjectsBinding
import java.time.Instant


class FinishedProjectsFragment : Fragment() {
    private var email: String? = null
    private var password : String? = null
    private var refreshToken : String? = null
    private var leaderId : String? = null

    private val userViewModel: UserViewModel by activityViewModels()

    private lateinit var infoTV : TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var refresh: SwipeRefreshLayout

    private lateinit var snackbar : Snackbar

    private lateinit var keycloakAPI : KeycloakAPI
    private lateinit var projectAPI : ProjectAPI

    private lateinit var adapter: FinishedProjectsAdapter

    private lateinit var binding: FragmentFinishedProjectsBinding

    private lateinit var finishedProjects : ArrayList<ProjectDto>

    private lateinit var finishedProjectsViewModel: FinishedProjectsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        keycloakAPI = KeycloakAPI()
        projectAPI = ProjectAPI()

        finishedProjectsViewModel = ViewModelProvider(this).get(FinishedProjectsViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        email = userViewModel.email.value
        password = userViewModel.password.value
        refreshToken = userViewModel.refreshToken.value
        leaderId = userViewModel.leaderId.value

        binding = FragmentFinishedProjectsBinding.inflate(inflater, container, false)

        infoTV = binding.finishedProjectsTV
        recyclerView = binding.finishedProjectsRV
        refresh = binding.finishedProjectRefresh

        snackbar = com.dybich.collabdoapp.Snackbar(binding.root,binding.root.context)


        if(finishedProjectsViewModel.isSaved){

            if(finishedProjectsViewModel.projectList!!.isNotEmpty()){
                infoTV.visibility = View.GONE
                finishedProjects = finishedProjectsViewModel.projectList!!
                recyclerView.layoutManager = LinearLayoutManager(binding.root.context)
                adapter = FinishedProjectsAdapter(finishedProjects,refreshToken!!,email!!,password!!, binding.root)
                recyclerView.adapter = adapter



                val listener = object : FinishedProjectsAdapter.OnItemCLickListener {
                    override fun onItemCLick(position: Int) {
                        finishedProjects.removeAt(position)
                        adapter.notifyItemRemoved(position)
                        for (i in position until finishedProjects.size) {
                            adapter.notifyItemChanged(i)
                        }
                        if(finishedProjects.size == 0){
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
            performKeycloakAction(miliseconds,leaderId,1,ProjectStatus.Finished)
        }

        return binding.root
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
                        finishedProjects = list
                        recyclerView.layoutManager = LinearLayoutManager(binding.root.context)
                        adapter = FinishedProjectsAdapter(finishedProjects,refreshToken!!,email!!,password!!, binding.root)
                        recyclerView.adapter = adapter



                        val listener = object : FinishedProjectsAdapter.OnItemCLickListener {
                            override fun onItemCLick(position: Int) {
                                finishedProjects.removeAt(position)
                                adapter.notifyItemRemoved(position)
                                for (i in position until finishedProjects.size) {
                                    adapter.notifyItemChanged(i)
                                }
                                if(finishedProjects.size == 0){
                                    infoTV.visibility = View.VISIBLE
                                }
                            }

                        }
                        adapter.setOnItemCLickListener(listener)

                        finishedProjectsViewModel.isSaved = true
                        finishedProjectsViewModel.projectList = finishedProjects


                    }
                    else{
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