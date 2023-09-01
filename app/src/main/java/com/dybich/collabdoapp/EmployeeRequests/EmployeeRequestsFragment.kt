package com.dybich.collabdoapp.EmployeeRequests

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

    private lateinit var recyclerView: RecyclerView

    private lateinit var requestsAdapter : EmployeeRequestsAdapter

    private lateinit var requestList : List<EmployeeRequestDto>

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

        recyclerView = view.findViewById(R.id.employeeRequestsRV)

        performKeycloakAction()


        return view
    }


    private fun getEmployeeRequests( accessToken : String){
        leaderAPI.getEmployeesRequests(accessToken,
            onSuccess = {list ->

                if (list != null) {
                    if(list.isNotEmpty()){
                        requestList = list
                        recyclerView.layoutManager = LinearLayoutManager(view.context)
                        requestsAdapter = EmployeeRequestsAdapter(requestList,refreshToken!!,email!!,password!!, view)
                        recyclerView.adapter = requestsAdapter
                    }
                }
                else{
                    snackbar.show("ERROR")
                }
            },
            onFailure = {error->
                snackbar.show(error)
            })
    }

    private fun performKeycloakAction(){
        keycloakAPI.getFromRefreshToken(refreshToken!!,
            onSuccess = {data ->
                refreshToken = data.refresh_token
                getEmployeeRequests(data.access_token)
            },
            onFailure = {error->
                if(error == "Refresh token expired"){
                    keycloakAPI.getFromEmailAndPass(email!!,password!!,
                        onSuccess = {data ->
                            refreshToken = data.refresh_token
                            getEmployeeRequests(data.access_token)
                        },
                        onFailure = {err->
                            snackbar.show(err)
                        })
                }
                else{
                    snackbar.show(error)
                }

            })
    }


}