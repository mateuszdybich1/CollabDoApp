package com.dybich.collabdoapp.Tasks.WaitingTasks

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
import com.dybich.collabdoapp.TaskStatus
import com.dybich.collabdoapp.databinding.VerifyTaskBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class WaitingTasksBottomSheet : BottomSheetDialogFragment()  {

    companion object {
        fun newInstance(projectId:String,taskId : String, refreshToken: String, email:String,password:String, position:Int,isLeader:Boolean) = WaitingTasksBottomSheet().apply {
            arguments = Bundle().apply {
                putInt("position",position)
                putString("projectId", projectId)
                putString("taskId", taskId)
                putString("refreshToken",refreshToken)
                putString("email",email)
                putString("password",password)
                putBoolean("isLeader",isLeader)
            }
        }
    }

    private lateinit var projectId : String
    private lateinit var taskId : String
    private lateinit var refreshToken : String
    private lateinit var email : String
    private lateinit var password : String
    private var isLeader : Boolean = false
    private var position : Int = 1

    private lateinit var binding: VerifyTaskBottomSheetBinding
    private lateinit var approveButton : Button
    private lateinit var declineButton : Button

    private lateinit var snackbar : Snackbar

    private lateinit var keycloakAPI : KeycloakAPI
    private lateinit var taskAPI : TaskAPI

    private lateinit var approveTransition : ButtonTransition
    private lateinit var declineTransition : ButtonTransition

    interface OnItemCLickListenerMyTask {
        fun onItemCLick(position: Int)
    }


    private var listener: OnItemCLickListenerMyTask? = null

    public fun setOnItemCLickListener(onItemCLickListenerMyTask: WaitingTasksBottomSheet.OnItemCLickListenerMyTask) {
        listener = onItemCLickListenerMyTask
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        keycloakAPI= KeycloakAPI()
        taskAPI = TaskAPI()

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = VerifyTaskBottomSheetBinding.inflate(inflater,container,false)

        approveTransition = ButtonTransition(
            binding.verifyTaskBottomSheet,
            binding.verifyApproveBottomSheetCircle,
            binding.verifyApproveBottomSheetButton,
            binding.root.context)

        declineTransition = ButtonTransition(
            binding.verifyTaskBottomSheet,
            binding.verifyDeclineBottomSheetCircle,
            binding.verifyDeclineBottomSheetButton,
            binding.root.context)

        approveButton = binding.verifyApproveBottomSheetButton
        declineButton = binding.verifyDeclineBottomSheetButton

        projectId = arguments?.getString("projectId").toString()
        taskId = arguments?.getString("taskId").toString()
        refreshToken = arguments?.getString("refreshToken").toString()
        email = arguments?.getString("email").toString()
        password = arguments?.getString("password").toString()
        position = arguments?.getInt("position")!!
        isLeader = arguments?.getBoolean("isLeader")!!

        snackbar = com.dybich.collabdoapp.Snackbar(binding.root,binding.root.context)

        approveButton.setOnClickListener{
            approveTransition.startLoading()
            performKeycloakAction(projectId,taskId,TaskStatus.Done)
        }

        declineButton.setOnClickListener{
            declineTransition.startLoading()
            performKeycloakAction(projectId,taskId,TaskStatus.Undone)
        }

        return binding.root
    }



    private fun verifyTask(accessToken:String, projectId: String, taskId: String,taskStatus: TaskStatus){
        taskAPI.changeTaskStatus(accessToken,projectId,taskId,taskStatus,isLeader,
            onSuccess = {

                if(projectId!=null){
                    Toast.makeText(binding.root.context,"Task approved", Toast.LENGTH_LONG).show()
                    listener?.onItemCLick(position)
                    dismiss()
                }
                approveTransition.stopLoading()
                declineTransition.stopLoading()
            },
            onFailure = {error->
                snackbar.show(error)
                approveTransition.stopLoading()
                declineTransition.stopLoading()
                dismiss()
            })
    }


    private fun performKeycloakAction(projectId:String, taskId : String, taskStatus: TaskStatus){
        keycloakAPI.getFromRefreshToken(refreshToken!!,
            onSuccess = {data ->
                refreshToken = data.refresh_token
                verifyTask(data.access_token,projectId,taskId,taskStatus)


            },
            onFailure = {error->
                if(error == "Refresh token expired" || error=="Token is not active"){
                    keycloakAPI.getFromEmailAndPass(email!!,password!!,
                        onSuccess = {data ->
                            refreshToken = data.refresh_token
                            verifyTask(data.access_token,projectId,taskId,taskStatus)

                        },
                        onFailure = {err->
                            snackbar.show(err)
                            approveTransition.stopLoading()
                            declineTransition.stopLoading()
                            dismiss()
                        })
                }
                else{
                    snackbar.show(error)
                    approveTransition.stopLoading()
                    declineTransition.stopLoading()
                    dismiss()
                }

            })
    }

}