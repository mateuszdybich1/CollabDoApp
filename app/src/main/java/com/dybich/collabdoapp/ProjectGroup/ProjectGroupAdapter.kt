package com.dybich.collabdoapp.EmployeeRequests

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dybich.collabdoapp.API.KeycloakAPI
import com.dybich.collabdoapp.API.LeaderAPI
import com.dybich.collabdoapp.Dtos.EmployeeDto
import com.dybich.collabdoapp.Dtos.EmployeeRequestDto
import com.dybich.collabdoapp.R
import com.dybich.collabdoapp.Snackbar

class ProjectGroupAdapter(private var list : List<EmployeeDto>, private var refreshToken : String, private var email : String, private var password : String,private var isLeader:Boolean, private var view:View) : RecyclerView.Adapter<ProjectGroupViewHolder> () {
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectGroupViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context).inflate(R.layout.project_group_member_item, parent, false)
        return ProjectGroupViewHolder(layoutInflater)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ProjectGroupViewHolder, position: Int) {

        keycloakAPI = KeycloakAPI()
        leaderAPI = LeaderAPI()

        snackbar = com.dybich.collabdoapp.Snackbar(view,view.context)

        holder.username.text =list[position].username
        holder.email.text = list[position].email

        if(isLeader){
            holder.deleteEmployee.setOnClickListener(){
                performKeycloakAction(list[position].employeeId!!)
                listener.onItemCLick(position)
            }
        }
        else{
            holder.deleteEmployee.visibility = View.GONE
        }


    }


    private fun removeFromGroup( accessToken : String, employeeId:String){
        leaderAPI.removeEmployeeFromGroup(accessToken,employeeId,
            onSuccess = {employeeId ->
                if (employeeId != null) {
                    snackbar.show("User removed from group")
                }
                else{
                    snackbar.show("ERROR")
                }
            },
            onFailure = {error->
                snackbar.show(error)
            })
    }

    private fun performKeycloakAction(employeeId: String){
        keycloakAPI.getFromRefreshToken(refreshToken,
            onSuccess = {data ->
                refreshToken = data.refresh_token
                removeFromGroup(data.access_token,employeeId)
            },
            onFailure = {error->
                if(error == "Refresh token expired" || error=="Token is not active"){
                    keycloakAPI.getFromEmailAndPass(email,password,
                        onSuccess = {data ->
                            refreshToken = data.refresh_token
                            removeFromGroup(data.access_token,employeeId)
                        },
                        onFailure = {err->

                        })
                }
            })
    }


}

class ProjectGroupViewHolder(private val itemView: View): RecyclerView.ViewHolder(itemView){
    val username : TextView = itemView.findViewById(R.id.memberUsername)
    val email : TextView = itemView.findViewById(R.id.memberEmail)

    val deleteEmployee : Button = itemView.findViewById(R.id.removeButton)


}