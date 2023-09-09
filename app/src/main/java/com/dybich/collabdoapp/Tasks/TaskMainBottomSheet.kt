package com.dybich.collabdoapp.Tasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.dybich.collabdoapp.API.KeycloakAPI
import com.dybich.collabdoapp.API.TaskAPI
import com.dybich.collabdoapp.ButtonTransition
import com.dybich.collabdoapp.Snackbar
import com.dybich.collabdoapp.Tasks.MyTasks.MyTasksBottomSheet
import com.dybich.collabdoapp.databinding.TaskMainBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class TaskMainBottomSheet : BottomSheetDialogFragment() {
    companion object {
        fun newInstance(projectId:String,taskId : String, refreshToken: String, email:String,password:String, position:Int) = TaskMainBottomSheet().apply {
            arguments = Bundle().apply {
                putInt("position",position)
                putString("projectId", projectId)
                putString("taskId", taskId)
                putString("refreshToken",refreshToken)
                putString("email",email)
                putString("password",password)
            }
        }
    }

    private lateinit var projectId : String
    private lateinit var taskId : String
    private lateinit var refreshToken : String
    private lateinit var email : String
    private lateinit var password : String
    private var position : Int = 1

    private lateinit var binding: TaskMainBottomSheetBinding
    private lateinit var button : Button

    private lateinit var snackbar : Snackbar

    private lateinit var keycloakAPI : KeycloakAPI
    private lateinit var taskAPI : TaskAPI

    private lateinit var transition : ButtonTransition

    interface OnItemCLickListenerMainTask {
        fun onItemCLick(position: Int)
    }


    private var listener: OnItemCLickListenerMainTask? = null


    public fun setOnItemCLickListener(onItemCLickListenerMainTask: TaskMainBottomSheet.OnItemCLickListenerMainTask) {
        listener = onItemCLickListenerMainTask
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        keycloakAPI= KeycloakAPI()
        taskAPI = TaskAPI()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = TaskMainBottomSheetBinding.inflate(inflater, container, false)
        transition = ButtonTransition(
            binding.taskMainBottomSheetLayout,
            binding.taskMainBottomSheetCircle,
            binding.taskMainBottomSheetButton,
            binding.root.context)

        button = binding.taskMainBottomSheetButton

        projectId = arguments?.getString("projectId").toString()
        taskId = arguments?.getString("taskId").toString()
        refreshToken = arguments?.getString("refreshToken").toString()
        email = arguments?.getString("email").toString()
        password = arguments?.getString("password").toString()
        position = arguments?.getInt("position")!!

        snackbar = com.dybich.collabdoapp.Snackbar(binding.root,binding.root.context)

        button.setOnClickListener{
            transition.startLoading()
            performKeycloakAction(projectId,taskId)
        }

        return binding.root
    }

    private fun deleteTask(accessToken:String,projectId: String,taskId:String){
        taskAPI.deleteTask(accessToken,projectId,taskId,
            onSuccess = {
                if(projectId!=null){
                    Toast.makeText(binding.root.context,"Task deleted", Toast.LENGTH_LONG).show()
                    listener?.onItemCLick(position)
                    dismiss()
                }
                transition.stopLoading()
            },
            onFailure = {error->
                snackbar.show(error)
                transition.stopLoading()
                dismiss()
            })
    }

    private fun performKeycloakAction(projectId:String, taskId : String){
        keycloakAPI.getFromRefreshToken(refreshToken!!,
            onSuccess = {data ->
                refreshToken = data.refresh_token
                deleteTask(data.access_token,projectId,taskId)

            },
            onFailure = {error->
                if(error == "Refresh token expired" || error=="Token is not active"){
                    keycloakAPI.getFromEmailAndPass(email!!,password!!,
                        onSuccess = {data ->
                            refreshToken = data.refresh_token
                            deleteTask(data.access_token,projectId,taskId)

                        },
                        onFailure = {err->
                            snackbar.show(err)
                            transition.stopLoading()
                            dismiss()
                        })
                }
                else{
                    snackbar.show(error)
                    transition.stopLoading()
                    dismiss()
                }

            })
    }


}