package com.dybich.collabdoapp.Projects

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dybich.collabdoapp.API.KeycloakAPI
import com.dybich.collabdoapp.API.LeaderAPI
import com.dybich.collabdoapp.Dtos.EmployeeDto
import com.dybich.collabdoapp.Dtos.ProjectDto
import com.dybich.collabdoapp.Priority
import com.dybich.collabdoapp.R
import com.dybich.collabdoapp.Snackbar

class ProjectsAdapter(private var list : List<ProjectDto>, private var refreshToken : String, private var email : String, private var password : String, private var view:View) : RecyclerView.Adapter<ProjectsViewHolder> () {
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context).inflate(R.layout.project_item, parent, false)
        return ProjectsViewHolder(layoutInflater)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ProjectsViewHolder, position: Int) {

        keycloakAPI = KeycloakAPI()
        leaderAPI = LeaderAPI()

        snackbar = com.dybich.collabdoapp.Snackbar(view,view.context)

        holder.projectName.text =list[position].name
        holder.priority.text = list[position].priority.name
    }

    private fun performKeycloakAction(employeeId: String){
        keycloakAPI.getFromRefreshToken(refreshToken,
            onSuccess = {data ->
                refreshToken = data.refresh_token

            },
            onFailure = {error->
                if(error == "Refresh token expired" || error=="Token is not active"){
                    keycloakAPI.getFromEmailAndPass(email,password,
                        onSuccess = {data ->
                            refreshToken = data.refresh_token

                        },
                        onFailure = {err->

                        })
                }
            })
    }


}

class ProjectsViewHolder(private val itemView: View): RecyclerView.ViewHolder(itemView){
    val projectName : TextView = itemView.findViewById(R.id.projectName)
    val priority : TextView = itemView.findViewById(R.id.projectPriority)


}