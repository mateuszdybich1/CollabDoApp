package com.dybich.collabdoapp.Tasks.ProjectTasks

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.dybich.collabdoapp.Dtos.TaskDto
import com.dybich.collabdoapp.LoggedInActivity
import com.dybich.collabdoapp.R
import com.dybich.collabdoapp.TaskDetails.CommentViewModel
import com.dybich.collabdoapp.Tasks.MyTasks.MyTasksBottomSheet
import com.dybich.collabdoapp.Tasks.TaskMainBottomSheet
import com.dybich.collabdoapp.Tasks.WaitingTasks.WaitingTasksBottomSheet
import java.text.SimpleDateFormat

class ProjectTasksAdapter(private var list : ArrayList<TaskDto>,private var refreshToken : String, private var email : String, private var password : String,private var isLeader : Boolean, private var view:View,private var viewType:String,private var navController:NavController) : RecyclerView.Adapter<ProjectTasksViewHolder> () {


    interface OnItemClickListenerProject {
        fun onItemCLick(position: Int)
    }
    private var listener: OnItemClickListenerProject? = null

    fun setOnItemClickListener(listener: OnItemClickListenerProject) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectTasksViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context).inflate(R.layout.task_item, parent, false)
        return ProjectTasksViewHolder(layoutInflater)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onBindViewHolder(holder: ProjectTasksViewHolder, position: Int) {

        val commentViewModel : CommentViewModel = ViewModelProvider(view.context as LoggedInActivity).get(CommentViewModel::class.java)

        holder.projectName.text =list[position].name

        holder.deadline.text = SimpleDateFormat("yyyy-MM-dd HH:mm").format(list[position].deadline)
        holder.priority.text = list[position].priority.name


            holder.card.setOnLongClickListener {
                val manager: FragmentManager = (it.context as FragmentActivity).supportFragmentManager
                if(viewType == "MyTasks"){
                    val myTasksBottomSheet = MyTasksBottomSheet.newInstance(list[position].projectId,list[position].taskId!!, refreshToken, email, password,position,isLeader)

                    myTasksBottomSheet.show(manager, myTasksBottomSheet.tag)

                    myTasksBottomSheet.setOnItemCLickListener(object : MyTasksBottomSheet.OnItemCLickListenerMyTask {
                        override fun onItemCLick(position: Int) {
                            listener?.onItemCLick(position)
                        }
                    })
                }
                if(isLeader){
                    if(viewType == "WaitingTasks"){

                        val waitingTasksBottomSheet = WaitingTasksBottomSheet.newInstance(list[position].projectId,list[position].taskId!!, refreshToken, email, password,position,isLeader)

                        waitingTasksBottomSheet.show(manager, waitingTasksBottomSheet.tag)
                        waitingTasksBottomSheet.setOnItemCLickListener(object : WaitingTasksBottomSheet.OnItemCLickListenerMyTask {
                            override fun onItemCLick(position: Int) {
                                listener?.onItemCLick(position)
                            }
                        })


                    }
                    else if(viewType == "AllTasks"){

                        val bottomSheet = TaskMainBottomSheet.newInstance(list[position].projectId,list[position].taskId!!, refreshToken, email, password,position)
                        bottomSheet.show(manager, bottomSheet.tag)

                        bottomSheet.setOnItemCLickListener(object : TaskMainBottomSheet.OnItemCLickListenerMainTask {
                            override fun onItemCLick(position: Int) {
                                listener?.onItemCLick(position)
                            }
                        })
                    }
                }

                true
            }



        holder.card.setOnClickListener{



            commentViewModel.taskId.value = list[position].taskId
            commentViewModel.name.value = list[position].name
            commentViewModel.description.value = list[position].description
            commentViewModel.priority.value = list[position].priority
            commentViewModel.assignedUser.value = list[position].userEmail
            commentViewModel.taskStatus.value = list[position].status
            commentViewModel.deadline.value = list[position].deadline
            navController.navigate(R.id.taskDetailsFragment)
            view.visibility = View.GONE

        }


    }

}

class ProjectTasksViewHolder(private val itemView: View): RecyclerView.ViewHolder(itemView){
    val projectName : TextView = itemView.findViewById(R.id.taskName)
    val priority : TextView = itemView.findViewById(R.id.taskPriority)
    val deadline : TextView = itemView.findViewById(R.id.taskDeadline)

    val card: CardView = itemView.findViewById(R.id.taskCard)


}