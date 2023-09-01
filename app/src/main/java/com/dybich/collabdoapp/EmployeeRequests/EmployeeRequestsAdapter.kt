package com.dybich.collabdoapp.EmployeeRequests

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dybich.collabdoapp.API.KeycloakAPI
import com.dybich.collabdoapp.API.LeaderAPI
import com.dybich.collabdoapp.Dtos.EmployeeRequestDto
import com.dybich.collabdoapp.R
import com.dybich.collabdoapp.Snackbar
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class EmployeeRequestsAdapter(private var requestList : List<EmployeeRequestDto>, private var refreshToken : String, private var email : String, private var password : String, private var view:View) : RecyclerView.Adapter<EmployeeRequestsViewHolder> () {
    private lateinit var keycloakAPI : KeycloakAPI
    private lateinit var leaderAPI : LeaderAPI

    private lateinit var snackbar : Snackbar

    interface OnItemCLickListener{
        fun onItemCLick(position : Int)
    }

    private lateinit var listener : OnItemCLickListener

    public fun setOnItemCLickListener(onItemCLickListener: OnItemCLickListener){
        listener = onItemCLickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeRequestsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context).inflate(R.layout.employee_request_item, parent, false)
        return EmployeeRequestsViewHolder(layoutInflater)
    }

    override fun getItemCount(): Int {
        return requestList.size
    }

    override fun onBindViewHolder(holder: EmployeeRequestsViewHolder, position: Int) {

        keycloakAPI = KeycloakAPI()
        leaderAPI = LeaderAPI()

        snackbar = com.dybich.collabdoapp.Snackbar(view,view.context)

        holder.username.text =requestList[position].username
        holder.email.text = requestList[position].email

        holder.acceptRequestButton.setOnClickListener(){
            performKeycloakAction("accept", requestList[position])
            listener.onItemCLick(position)

        }

        holder.deleteRequestButton.setOnClickListener(){
            performKeycloakAction("delete",requestList[position])
            listener.onItemCLick(position)
        }
    }

    private fun acceptRequest( accessToken : String, employeeRequestDto: EmployeeRequestDto){
        leaderAPI.acceptRequest(accessToken,employeeRequestDto.employeeRequestId,
            onSuccess = {employeeId ->

                if (employeeId != null) {
                    snackbar.show("User ${employeeRequestDto.username}request accepted")
                }
                else{
                    snackbar.show("ERROR")
                }
            },
            onFailure = {error->
                snackbar.show(error)
            })
    }

    private fun deleteRequest( accessToken : String, employeeRequestDto: EmployeeRequestDto){
        leaderAPI.deleteRequest(accessToken,employeeRequestDto.employeeRequestId,
            onSuccess = {employeeId ->
                if (employeeId != null) {
                    snackbar.show("User ${employeeRequestDto.username} request deleted")
                }
                else{
                    snackbar.show("ERROR")
                }
            },
            onFailure = {error->
                snackbar.show(error)
            })
    }

    private fun performKeycloakAction(requestType:String,employeeRequestDto : EmployeeRequestDto){
        keycloakAPI.getFromRefreshToken(refreshToken,
            onSuccess = {data ->
                refreshToken = data.refresh_token
                if(requestType == "accept"){
                    acceptRequest(data.access_token,employeeRequestDto)
                }
                else if(requestType == "delete"){
                    deleteRequest(data.access_token,employeeRequestDto)
                }
            },
            onFailure = {error->
                if(error == "Refresh token expired" || error=="Token is not active"){
                    keycloakAPI.getFromEmailAndPass(email,password,
                        onSuccess = {data ->
                            refreshToken = data.refresh_token

                            if(requestType == "accept"){
                                acceptRequest(data.access_token,employeeRequestDto)
                            }
                            else if(requestType == "delete"){
                                deleteRequest(data.access_token,employeeRequestDto)
                            }
                        },
                        onFailure = {err->

                        })
                }
            })
    }


}

class EmployeeRequestsViewHolder(private val itemView: View): RecyclerView.ViewHolder(itemView){
    val username : TextView = itemView.findViewById(R.id.userUsername)
    val email : TextView = itemView.findViewById(R.id.userEmail)

    val acceptRequestButton : FloatingActionButton = itemView.findViewById(R.id.acceptButton)
    val deleteRequestButton : FloatingActionButton = itemView.findViewById(R.id.cancelButton)

}