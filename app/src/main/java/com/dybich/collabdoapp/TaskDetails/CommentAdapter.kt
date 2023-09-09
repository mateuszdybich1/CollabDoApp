package com.dybich.collabdoapp.TaskDetails

import android.annotation.SuppressLint
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.dybich.collabdoapp.Dtos.CommentDto
import com.dybich.collabdoapp.R
import com.dybich.collabdoapp.Tasks.MyTasks.MyTasksBottomSheet

class CommentAdapter(private var list : ArrayList<CommentDto>, private var taskId : String,private var refreshToken : String, private var email : String, private var password : String, private var isLeader : Boolean) : RecyclerView.Adapter<CommentViewHolder>() {

    interface OnItemClickListenerComment {
        fun onItemCLick(position: Int)
    }
    private var listener: OnItemClickListenerComment? = null

    fun setOnItemClickListener(listener: OnItemClickListenerComment) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context).inflate(R.layout.comment_item, parent, false)
        return CommentViewHolder(layoutInflater)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {


        holder.content.text = list[position].content

        if(list[position].author == email){
            val layoutParams = holder.card.layoutParams as LinearLayout.LayoutParams
            layoutParams.gravity = Gravity.END

            holder.card.layoutParams = layoutParams
            holder.card.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
            holder.content.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.secondaryBlue))

         }

        else{
            val layoutParams = holder.card.layoutParams as LinearLayout.LayoutParams
            layoutParams.gravity = Gravity.START
            holder.card.layoutParams = layoutParams
            holder.card.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.secondaryBlue))
            holder.content.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.white))

        }
    }


}
 class CommentViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
    var card : CardView = itemView.findViewById(R.id.commentCard)
    var content : TextView = itemView.findViewById(R.id.commentContent)

}

