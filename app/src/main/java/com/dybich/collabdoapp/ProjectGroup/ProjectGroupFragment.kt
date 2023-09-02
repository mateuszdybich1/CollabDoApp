package com.dybich.collabdoapp.ProjectGroup

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.dybich.collabdoapp.API.EmployeeAPI
import com.dybich.collabdoapp.API.KeycloakAPI
import com.dybich.collabdoapp.API.LeaderAPI
import com.dybich.collabdoapp.Dtos.EmployeeDto
import com.dybich.collabdoapp.EmployeeRequests.EmployeeRequestsAdapter
import com.dybich.collabdoapp.EmployeeRequests.ProjectGroupAdapter
import com.dybich.collabdoapp.LoggedInActivity
import com.dybich.collabdoapp.R
import com.dybich.collabdoapp.SharedViewModel
import com.dybich.collabdoapp.Snackbar
import com.dybich.collabdoapp.login.LoginActivity


class ProjectGroupFragment : Fragment() {

    private var email: String? = null
    private var password : String? = null
    private var refreshToken : String? = null
    private var isLeader : Boolean? = null
    private var leaderId : String? = null

    private lateinit var snackbar : Snackbar

    private lateinit var keycloakAPI : KeycloakAPI
    private lateinit var leaderAPI : LeaderAPI
    private lateinit var employeeAPI: EmployeeAPI

    private var leaderEmail: String = "Loading"
    private lateinit var leader: TextView
    private lateinit var infoTV : TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var quitButton : Button
    private lateinit var refresh: SwipeRefreshLayout

    private lateinit var adapter: ProjectGroupAdapter

    private val sharedViewModel: SharedViewModel by activityViewModels()


    private lateinit var groupMembers : ArrayList<EmployeeDto>

    private lateinit var view : View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        keycloakAPI = KeycloakAPI()
        leaderAPI = LeaderAPI()
        employeeAPI = EmployeeAPI()

        email = sharedViewModel.email.value
        password = sharedViewModel.password.value
        refreshToken = sharedViewModel.refreshToken.value
        isLeader = sharedViewModel.isLeader.value
        leaderId = sharedViewModel.leaderId.value





        if(!isLeader!!){

            getLeaderEmail(leaderId!!)
        }
        else{
            leaderEmail = email.toString()
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        view = inflater.inflate(R.layout.fragment_project_group, container, false)

        snackbar = com.dybich.collabdoapp.Snackbar(view,view.context)

        leader = view.findViewById(R.id.leaderEmail)
        leader.text = leaderEmail

        quitButton = view.findViewById(R.id.leaveProject)
        refresh = view.findViewById(R.id.projectGroupRefresh)
        infoTV = view.findViewById(R.id.projectGroupTV)
        recyclerView = view.findViewById(R.id.projectGroupRV)

        if(isLeader!!){
            quitButton.visibility = View.GONE
        }

        performKeycloakAction()

        refresh.setOnRefreshListener {
            performKeycloakAction()
        }

        return view
    }



    private fun getLeaderEmail(leaderId:String){
        leaderAPI.getLeaderEmail(leaderId, onSuccess = {email->
              if(email != null){
                  leader.text = email
              }
              else{
                  snackbar.show("ERROR")
              }
        }, onFailure = {error->
            snackbar.show(error)
        } )
    }

    private fun getEmployees( accessToken : String){
        leaderAPI.getEmployeeList(accessToken,
            leaderId,
            onSuccess = {list ->
                refresh.isRefreshing = false
                if (list != null) {
                    if(list.isNotEmpty()){
                        infoTV.visibility = View.GONE
                        groupMembers = list
                        recyclerView.layoutManager = LinearLayoutManager(view.context)
                        adapter = ProjectGroupAdapter(groupMembers,refreshToken!!,email!!,password!!,isLeader!!, view)
                        recyclerView.adapter = adapter



                        val listener = object : ProjectGroupAdapter.OnItemCLickListener {
                            override fun onItemCLick(position: Int) {
                                groupMembers.removeAt(position)
                                adapter.notifyItemRemoved(position)
                                for (i in position until groupMembers.size) {
                                    adapter.notifyItemChanged(i)
                                }
                                if(groupMembers.size == 0){
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
                else{
                    snackbar.show("ERROR")
                    infoTV.visibility = View.VISIBLE
                    refresh.isRefreshing = false
                }
            },
            onFailure = {error->
                snackbar.show(error)
                infoTV.visibility = View.VISIBLE
                refresh.isRefreshing = false
            })
    }

    private fun performKeycloakAction(){
        keycloakAPI.getFromRefreshToken(refreshToken!!,
            onSuccess = {data ->
                refreshToken = data.refresh_token
                getEmployees(data.access_token)

            },
            onFailure = {error->
                if(error == "Refresh token expired" || error=="Token is not active"){
                    keycloakAPI.getFromEmailAndPass(email!!,password!!,
                        onSuccess = {data ->
                            refreshToken = data.refresh_token

                            getEmployees(data.access_token)

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