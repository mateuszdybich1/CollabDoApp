package com.dybich.collabdoapp.EmployeeRequests

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.dybich.collabdoapp.API.KeycloakAPI
import com.dybich.collabdoapp.API.LeaderAPI
import com.dybich.collabdoapp.Dtos.EmployeeRequestDto
import com.dybich.collabdoapp.R
import com.dybich.collabdoapp.SharedViewModel
import com.dybich.collabdoapp.Snackbar


class EmployeeRequestsFragment : Fragment() {

    private var email: String? = null
    private var password : String? = null
    private var refreshToken : String? = null

    private lateinit var snackbar : Snackbar

    private lateinit var keycloakAPI : KeycloakAPI
    private lateinit var leaderAPI : LeaderAPI

    private lateinit var infoTV: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var refresh: SwipeRefreshLayout

    private lateinit var requestsAdapter : EmployeeRequestsAdapter

    private lateinit var requestList : ArrayList<EmployeeRequestDto>

    private val sharedViewModel: SharedViewModel by activityViewModels()

    private lateinit var view : View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        keycloakAPI = KeycloakAPI()
        leaderAPI = LeaderAPI()


        email = sharedViewModel.email.value
        password = sharedViewModel.password.value
        refreshToken = sharedViewModel.refreshToken.value


        view = inflater.inflate(R.layout.fragment_employee_requests, container, false)
        snackbar = com.dybich.collabdoapp.Snackbar(view,view.context)


        infoTV = view.findViewById(R.id.employeeRequestTV)
        recyclerView = view.findViewById(R.id.employeeRequestsRV)
        refresh = view.findViewById(R.id.employeeRequestsRefresh)


        performKeycloakAction()


        refresh.setOnRefreshListener {
            performKeycloakAction()
        }


        return view
    }


    private fun getEmployeeRequests( accessToken : String){
        leaderAPI.getEmployeesRequests(accessToken,
            onSuccess = {list ->
                refresh.isRefreshing = false
                if (list != null) {
                    if(list.isNotEmpty()){
                        infoTV.visibility = View.GONE
                        requestList = list
                        recyclerView.layoutManager = LinearLayoutManager(view.context)
                        requestsAdapter = EmployeeRequestsAdapter(requestList,refreshToken!!,email!!,password!!, view)
                        recyclerView.adapter = requestsAdapter



                        val listener = object : EmployeeRequestsAdapter.OnItemCLickListener {
                            override fun onItemCLick(position: Int) {
                                requestList.removeAt(position)
                                requestsAdapter.notifyItemRemoved(position)
                                for (i in position until requestList.size) {
                                    requestsAdapter.notifyItemChanged(i)
                                }
                                if(requestList.size == 0){
                                    infoTV.visibility = View.VISIBLE
                                }
                            }

                        }
                        requestsAdapter.setOnItemCLickListener(listener)
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
                getEmployeeRequests(data.access_token)
            },
            onFailure = {error->
                if(error == "Refresh token expired" || error=="Token is not active"){
                    keycloakAPI.getFromEmailAndPass(email!!,password!!,
                        onSuccess = {data ->
                            refreshToken = data.refresh_token
                            getEmployeeRequests(data.access_token)
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