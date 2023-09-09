package com.dybich.collabdoapp.TaskDetails

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dybich.collabdoapp.*
import com.dybich.collabdoapp.API.CommentAPI
import com.dybich.collabdoapp.API.KeycloakAPI
import com.dybich.collabdoapp.Dtos.CommentDto
import com.dybich.collabdoapp.Tasks.RefreshLayoutViewModel
import com.dybich.collabdoapp.databinding.FragmentTaskDetailsBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.time.Instant


class TaskDetailsFragment : Fragment() {


    private lateinit var binding : FragmentTaskDetailsBinding
    private lateinit var backButton: FloatingActionButton

    private var email: String? = null
    private var password : String? = null
    private var refreshToken : String? = null
    private var isLeader:Boolean?=false

    private var taskId:String?=null
    private var name:String?=null
    private var description:String?=null
    private var priority:Priority?=Priority.Low
    private var assignedUser:String?=null
    private var taskSatus : TaskStatus?=TaskStatus.Started
    private var deadline:Long?=1

    private lateinit var nameTV : TextView
    private lateinit var descriptionTV : TextView
    private lateinit var priorityTV : TextView
    private lateinit var assignedUserTV : TextView
    private lateinit var taskSatusTV : TextView
    private lateinit var deadlineTV : TextView


    private lateinit var addCommentETL : TextInputLayout
    private lateinit var addCommentET : TextInputEditText
    private lateinit var recyclerView:RecyclerView
    private lateinit var addComment:ImageButton
    private lateinit var loadingCircle : CircularProgressIndicator
    private lateinit var loadMoreComments : CircularProgressIndicator

    private val userViewModel: UserViewModel by activityViewModels()
    private val sharedViewModel: RefreshLayoutViewModel by activityViewModels()
    private val commentViewModel: CommentViewModel by activityViewModels()

    private lateinit var snackbar : Snackbar

    private lateinit var keycloakAPI: KeycloakAPI
    private lateinit var commentAPI : CommentAPI

    private lateinit var adapter:CommentAdapter
    private var pageNumber:Int = 1
    private var requestDate:Long = 1
    private lateinit var commentList : ArrayList<CommentDto>

    private lateinit var  runnable:  Runnable
    private val handler = Handler(Looper.getMainLooper())

    private var isLoading = false
    private var isListening = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        keycloakAPI = KeycloakAPI()
        commentAPI = CommentAPI()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        email = userViewModel.email.value
        password = userViewModel.password.value
        refreshToken = userViewModel.refreshToken.value
        isLeader = userViewModel.isLeader.value

        taskId = commentViewModel.taskId.value
        name = commentViewModel.name.value
        description = commentViewModel.description.value
        priority = commentViewModel.priority.value
        assignedUser = commentViewModel.assignedUser.value
        taskSatus = commentViewModel.taskStatus.value
        deadline = commentViewModel.deadline.value


        binding = FragmentTaskDetailsBinding.inflate(inflater, container, false)

        nameTV = binding.taskDetailsName
        descriptionTV = binding.taskDetailsDescription
        priorityTV = binding.taskPriority
        assignedUserTV = binding.userEmail
        taskSatusTV = binding.taskStatus
        deadlineTV = binding.taskDeadline

        backButton = binding.fragmentTaskDetailsBackButton

        addCommentETL = binding.addCommentETL
        addCommentET = binding.addCommentET
        recyclerView = binding.commentsRV
        addComment = binding.sendButton

        loadingCircle = binding.commentsLoadingCircle
        loadMoreComments = binding.loadMoreComments


        nameTV.text = name
        descriptionTV.text =description
        priorityTV.text = priority?.name.toString()
        assignedUserTV.text = assignedUser.toString()
        taskSatusTV.text = taskSatus?.name.toString()
        deadlineTV.text = SimpleDateFormat("yyyy-MM-dd HH:mm").format(deadline)

        addCommentET.addTextChangedListener{
            addCommentETL.error = null
        }

        snackbar = com.dybich.collabdoapp.Snackbar(binding.root,binding.root.context)


        backButton.setOnClickListener{
            sharedViewModel.shouldRefresh.value = true
            findNavController().popBackStack()


        }


        addComment.setOnClickListener{
            if(addCommentET.text!!.length in 1..100){
                if(taskId!=null){
                    performKeycloakAction(taskId!!,"addComment",addCommentET.text.toString(),null,null)
                }

            }
            else{
                if(addCommentET.text!!.length == 0){
                    addCommentETL.error = "empty comment"
                }
                else if(addCommentET.text!!.length >100){
                    addCommentETL.error = "too long"

                }
            }
        }

        if(taskId!=null){
            loadingCircle.visibility = View.VISIBLE
            val now : Instant = Instant.now()
            requestDate = now.toEpochMilli()
            pageNumber = 1
            performKeycloakAction(taskId!!,"getComments",null,requestDate,pageNumber)
        }


        return binding.root
    }

    private fun startRequestLoop() {

        getLatestComment(taskId!!,commentList[0].commentId)
    }

    private fun getLatestComment(taskId : String, latestCommentId:String){

        keycloakAPI.getFromRefreshToken(refreshToken!!,
            onSuccess = { data ->

                refreshToken = data.refresh_token

                commentAPI.getLatestComment(data.access_token,taskId,latestCommentId,
                    onSuccess = {latestComment->

                        commentList.add(0, latestComment)

                        adapter.notifyItemInserted(0)

                        recyclerView.scrollToPosition(0)
                        startRequestLoop()


                    }, onFailure = {error->

                        if(error == "404"){
                            startRequestLoop()

                        }
                        else{
                            snackbar.show(error)
                        }
                    })


            },
            onFailure = { error ->
                if (error == "Refresh token expired" || error == "Token is not active") {
                    keycloakAPI.getFromEmailAndPass(email!!, password!!,
                        onSuccess = { data ->
                            refreshToken = data.refresh_token

                            commentAPI.getLatestComment(data.access_token,taskId,latestCommentId,
                                onSuccess = {latestComment->

                                    commentList.add(0, latestComment)

                                    adapter.notifyItemInserted(0)

                                    recyclerView.scrollToPosition(0)
                                    startRequestLoop()

                                }, onFailure = {error->

                                    if(error == "404"){
                                        startRequestLoop()

                                    }
                                    else{
                                        snackbar.show(error)
                                    }

                                })

                        },
                        onFailure = { err ->
                            isListening = true
                            snackbar.show(err)
                        })
                } else {
                    isListening = true
                    snackbar.show(error)
                }

            })


    }




    private fun addComment(accessToken:String, taskId:String,content:String ){
        commentAPI.addComment(accessToken,taskId,content,
            onSuccess = {commentId->
               addCommentET.setText("")
            },
            onFailure = {error->
                snackbar.show(error)
            })
    }





    private fun getComments(accessToken:String, taskId : String, requestDate:Long){
        commentAPI.getComments(accessToken,taskId,pageNumber,requestDate,
            onSuccess = {list->

                if (isAdded) {
                    if (list != null) {
                        if (list.isNotEmpty()) {
                            if (pageNumber == 1) {
                                commentList = list
                                val layoutManager = LinearLayoutManager(binding.root.context)
                                layoutManager.reverseLayout = true

                                layoutManager.scrollToPosition(0)

                                recyclerView.layoutManager = layoutManager


                                adapter = CommentAdapter(
                                    commentList,
                                    taskId,
                                    refreshToken!!,
                                    email!!,
                                    password!!,
                                    isLeader!!)
                                recyclerView.adapter = adapter


                                val listener = object : CommentAdapter.OnItemClickListenerComment {
                                    override fun onItemCLick(position: Int) {
                                        commentList.removeAt(position)
                                        adapter.notifyItemRemoved(position)
                                        for (i in position until commentList.size) {
                                            adapter.notifyItemChanged(i)
                                        }
                                    }

                                }
                                adapter.setOnItemClickListener(listener)
                                startRequestLoop()


                            } else if (pageNumber > 1) {
                                loadMoreComments.visibility = View.GONE
                                commentList.addAll(list)
                                adapter.notifyDataSetChanged()
                            }
                            if (list.size == 0 && pageNumber >1) {
                                startRequestLoop()
                                loadMoreComments.visibility = View.GONE
                                recyclerView.clearOnScrollListeners()
                                isLoading = true
                            }
                            if (list.size > 0) {
                                isLoading = false
                            }

                            if (list.size == 5) {
                                recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                                        super.onScrolled(recyclerView, dx, dy)

                                        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                                        val visibleItemCount = layoutManager.childCount
                                        val totalItemCount = layoutManager.itemCount
                                        val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()



                                        if (!isLoading && dy < 0 && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount) {
                                            isLoading = true
                                            loadMoreComments.visibility = View.VISIBLE
                                            pageNumber += 1
                                            performKeycloakAction(
                                                taskId,
                                                "getComments",
                                                null,
                                                requestDate,
                                                pageNumber
                                            )
                                        }
                                    }
                                })
                            }

                        } else {
                            if (pageNumber > 1 && list.size == 0) {
                                snackbar.show("No new items")

                            }
                            loadMoreComments.visibility = View.GONE

                        }
                    }
                }
                loadingCircle.visibility = View.GONE
            },
            onFailure = {error->
                snackbar.show(error)
                loadMoreComments.visibility = View.GONE
                loadingCircle.visibility = View.GONE
            })
    }


    private fun performKeycloakAction(taskId : String, requestType:String,content:String?,requstDate :Long?,pageNumber: Int?) {
        keycloakAPI.getFromRefreshToken(refreshToken!!,
            onSuccess = { data ->
                refreshToken = data.refresh_token

                if(requestType == "addComment"){
                    if (content != null) {
                        addComment(data.access_token,taskId,content)
                    }
                }
                else if(requestType == "getComments"){
                    if (requstDate != null && pageNumber != null) {
                        getComments(data.access_token,taskId,requstDate)
                    }
                }


            },
            onFailure = { error ->
                if (error == "Refresh token expired" || error == "Token is not active") {
                    keycloakAPI.getFromEmailAndPass(email!!, password!!,
                        onSuccess = { data ->
                            refreshToken = data.refresh_token
                            if(requestType == "addComment"){
                                if (content != null) {
                                    addComment(data.access_token,taskId,content)
                                }
                            }
                            else if(requestType == "getComments"){
                                if (requstDate != null && pageNumber != null) {
                                    getComments(data.access_token,taskId,requstDate)
                                }
                            }
                        },
                        onFailure = { err ->
                            snackbar.show(err)
                            loadMoreComments.visibility = View.GONE
                            loadingCircle.visibility = View.GONE
                        })
                } else {
                    snackbar.show(error)
                    loadMoreComments.visibility = View.GONE
                    loadingCircle.visibility = View.GONE
                }

            })
    }


}