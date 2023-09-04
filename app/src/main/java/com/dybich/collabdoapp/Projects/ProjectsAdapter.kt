package com.dybich.collabdoapp.Projects

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.dybich.collabdoapp.API.KeycloakAPI
import com.dybich.collabdoapp.API.LeaderAPI
import com.dybich.collabdoapp.Dtos.ProjectDto
import com.dybich.collabdoapp.R
import com.dybich.collabdoapp.Snackbar

class ProjectsAdapter(private var list : ArrayList<ProjectDto>, private var refreshToken : String, private var email : String, private var password : String,private var isLeader : Boolean, private var view:View) : RecyclerView.Adapter<ProjectsViewHolder> () {


    interface OnItemClickListenerProject {
        fun onItemCLick(position: Int)
    }
    private var listener: OnItemClickListenerProject? = null

    fun setOnItemClickListener(listener: OnItemClickListenerProject) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context).inflate(R.layout.project_item, parent, false)
        return ProjectsViewHolder(layoutInflater)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ProjectsViewHolder, position: Int) {



        holder.projectName.text =list[position].name
        holder.priority.text = list[position].priority.name

        holder.card.setOnClickListener{
            val navController = Navigation.findNavController(view)

            navController.navigate(R.id.taskFragmentMain)

            view.visibility = View.GONE
        }

        if(isLeader){
            holder.card.setOnLongClickListener{

                val manager: FragmentManager = (it.context as FragmentActivity).supportFragmentManager
                val bottomSheet = ProjectsBottomSheet.newInstance(list[position].projectId!!, refreshToken, email, password,position)

                bottomSheet.show(manager, bottomSheet.tag)

                bottomSheet.setOnItemCLickListener(object : ProjectsBottomSheet.OnItemCLickListenerProject {
                    override fun onItemCLick(position: Int) {
                        listener?.onItemCLick(position)
                    }
                })
                true
            }


        }

    }

}

class ProjectsViewHolder(private val itemView: View): RecyclerView.ViewHolder(itemView){
    val projectName : TextView = itemView.findViewById(R.id.projectName)
    val priority : TextView = itemView.findViewById(R.id.projectPriority)

    val card: CardView = itemView.findViewById(R.id.projectCard)


}