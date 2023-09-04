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
import com.google.android.material.progressindicator.CircularProgressIndicator
import java.time.Instant


class FinishedProjectsFragment : Fragment() {
    private var email: String? = null
    private var password : String? = null
    private var refreshToken : String? = null
    private var leaderId : String? = null
    private var isLeader : Boolean? = false

    private val userViewModel: UserViewModel by activityViewModels()

    private var isLoading = false
    private lateinit var infoTV : TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var refresh: SwipeRefreshLayout
    private lateinit var loadMore : CircularProgressIndicator

    private lateinit var snackbar : Snackbar

    private lateinit var keycloakAPI : KeycloakAPI
    private lateinit var projectAPI : ProjectAPI

    private lateinit var adapter: FinishedProjectsAdapter

    private lateinit var binding: FragmentFinishedProjectsBinding


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
        isLeader = userViewModel.isLeader.value

        binding = FragmentFinishedProjectsBinding.inflate(inflater, container, false)

        infoTV = binding.finishedProjectsTV
        recyclerView = binding.finishedProjectsRV
        refresh = binding.finishedProjectRefresh
        loadMore = binding.finishedProjectsLoadMore
        snackbar = com.dybich.collabdoapp.Snackbar(binding.root,binding.root.context)


        if(finishedProjectsViewModel.isSaved){

            if(finishedProjectsViewModel.projectList!!.isNotEmpty()){
                infoTV.visibility = View.GONE

                recyclerView.layoutManager = LinearLayoutManager(binding.root.context)
                adapter = FinishedProjectsAdapter(finishedProjectsViewModel.projectList!!,refreshToken!!,email!!,password!!, isLeader!!, binding.root)
                recyclerView.adapter = adapter



                val listener = object : FinishedProjectsAdapter.OnItemCLickListener {
                    override fun onItemCLick(position: Int) {
                        finishedProjectsViewModel.projectList!!.removeAt(position)
                        adapter.notifyItemRemoved(position)
                        for (i in position until finishedProjectsViewModel.projectList!!.size) {
                            adapter.notifyItemChanged(i)
                        }
                        if(finishedProjectsViewModel.projectList!!.size == 0){
                            infoTV.visibility = View.VISIBLE
                        }
                    }

                }
                adapter.setOnItemCLickListener(listener)

                if(finishedProjectsViewModel.projectList!!.size % 10 == 0){
                    recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                            super.onScrolled(recyclerView, dx, dy)

                            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                            val visibleItemCount = layoutManager.childCount
                            val totalItemCount = layoutManager.itemCount
                            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                            val threshold = 10

                            if (!isLoading && dy > 0 && totalItemCount - visibleItemCount <= firstVisibleItemPosition + threshold) {

                                isLoading = true
                                loadMore.visibility = View.VISIBLE
                                finishedProjectsViewModel.pageNumber++
                                performKeycloakAction(finishedProjectsViewModel.miliseconds!!, leaderId, finishedProjectsViewModel.pageNumber, ProjectStatus.Finished)
                            }
                        }
                    })
                }
            }
            else{
                infoTV.visibility = View.VISIBLE
            }


        }
        else if(!finishedProjectsViewModel.isSaved){
            val now : Instant = Instant.now()
            finishedProjectsViewModel.miliseconds = now.toEpochMilli()
            finishedProjectsViewModel.pageNumber = 1
            performKeycloakAction(finishedProjectsViewModel.miliseconds!!,leaderId,finishedProjectsViewModel.pageNumber,ProjectStatus.Finished)
        }

        refresh.setOnRefreshListener {
            val now : Instant = Instant.now()
            finishedProjectsViewModel.miliseconds = now.toEpochMilli()
            finishedProjectsViewModel.pageNumber = 1
            performKeycloakAction(finishedProjectsViewModel.miliseconds!!,leaderId,finishedProjectsViewModel.pageNumber,ProjectStatus.Finished)
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
                    loadMore.visibility = View.GONE
                    if(list.isNotEmpty()){
                        if(finishedProjectsViewModel.pageNumber == 1){

                            infoTV.visibility = View.GONE
                            finishedProjectsViewModel.projectList = list
                            recyclerView.layoutManager = LinearLayoutManager(binding.root.context)
                            adapter = FinishedProjectsAdapter(finishedProjectsViewModel.projectList!!,refreshToken!!,email!!,password!!,isLeader!!, binding.root)
                            recyclerView.adapter = adapter



                            val listener = object : FinishedProjectsAdapter.OnItemCLickListener {
                                override fun onItemCLick(position: Int) {
                                    finishedProjectsViewModel.projectList!!.removeAt(position)
                                    adapter.notifyItemRemoved(position)
                                    for (i in position until finishedProjectsViewModel.projectList!!.size) {
                                        adapter.notifyItemChanged(i)
                                    }
                                    if(finishedProjectsViewModel.projectList!!.size == 0){
                                        infoTV.visibility = View.VISIBLE
                                    }
                                }

                            }
                            adapter.setOnItemCLickListener(listener)

                            finishedProjectsViewModel.isSaved = true

                            if(list.size == 10){
                                recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                                        super.onScrolled(recyclerView, dx, dy)

                                        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                                        val visibleItemCount = layoutManager.childCount
                                        val totalItemCount = layoutManager.itemCount
                                        val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                                        val threshold = 10

                                        if (!isLoading && dy > 0 && totalItemCount - visibleItemCount <= firstVisibleItemPosition + threshold) {
                                            isLoading = true
                                            loadMore.visibility = View.VISIBLE
                                            finishedProjectsViewModel.pageNumber++
                                            performKeycloakAction(finishedProjectsViewModel.miliseconds!!, leaderId, finishedProjectsViewModel.pageNumber, ProjectStatus.Finished)
                                        }
                                    }
                                })
                            }


                        }
                        else if(finishedProjectsViewModel.pageNumber >1){
                            loadMore.visibility = View.GONE
                            finishedProjectsViewModel.projectList?.addAll(list)
                            adapter.notifyDataSetChanged()
                        }
                        if (list.size == 0) {
                            loadMore.visibility = View.GONE
                            recyclerView.clearOnScrollListeners()
                        }

                        isLoading = false

                    }
                    else{
                        if(finishedProjectsViewModel.pageNumber>1 && list.size == 0){
                            snackbar.show("No new items")
                        }
                        else{
                            infoTV.visibility = View.VISIBLE
                        }
                        loadMore.visibility = View.GONE

                    }
                }
                else{
                    snackbar.show("ERROR")
                    infoTV.visibility = View.VISIBLE
                    refresh.isRefreshing = false
                    loadMore.visibility = View.GONE
                }

            }, onFailure ={error->
                refresh.isRefreshing = false
                snackbar.show(error)
                loadMore.visibility = View.GONE
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
                            infoTV.visibility = View.VISIBLE
                            refresh.isRefreshing = false
                            loadMore.visibility = View.GONE
                        })
                }
                else{
                    snackbar.show(error)
                    infoTV.visibility = View.VISIBLE
                    refresh.isRefreshing = false
                    loadMore.visibility = View.GONE
                }

            })
    }

}