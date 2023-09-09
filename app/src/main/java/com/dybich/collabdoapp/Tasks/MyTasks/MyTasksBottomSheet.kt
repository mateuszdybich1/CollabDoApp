package com.dybich.collabdoapp.Tasks.MyTasks

import android.os.Bundle
import androidx.fragment.app.Fragment
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
import com.dybich.collabdoapp.Tasks.TaskMainBottomSheet
import com.dybich.collabdoapp.databinding.FragmentMyTasksBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class MyTasksBottomSheet : BottomSheetDialogFragment()  {

    companion object {
        fun newInstance(projectId:String,taskId : String, refreshToken: String, email:String,password:String, position:Int,isLeader:Boolean) = MyTasksBottomSheet().apply {
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

    private lateinit var binding: FragmentMyTasksBottomSheetBinding
    private lateinit var button : Button

    private lateinit var snackbar : Snackbar

    private lateinit var keycloakAPI : KeycloakAPI
    private lateinit var taskAPI : TaskAPI

    private lateinit var transition : ButtonTransition

    interface OnItemCLickListenerMyTask {
        fun onItemCLick(position: Int)
    }


    private var listener: OnItemCLickListenerMyTask? = null

    public fun setOnItemCLickListener(onItemCLickListenerMyTask: MyTasksBottomSheet.OnItemCLickListenerMyTask) {
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

        binding = FragmentMyTasksBottomSheetBinding.inflate(inflater,container,false)

        transition = ButtonTransition(
            binding.myTasksBottomSheetLayout,
            binding.myTasksBottomSheetCircle,
            binding.myTasksBottomSheetButton,
            binding.root.context)

        button = binding.myTasksBottomSheetButton

        projectId = arguments?.getString("projectId").toString()
        taskId = arguments?.getString("taskId").toString()
        refreshToken = arguments?.getString("refreshToken").toString()
        email = arguments?.getString("email").toString()
        password = arguments?.getString("password").toString()
        position = arguments?.getInt("position")!!
        isLeader = arguments?.getBoolean("isLeader")!!

        snackbar = com.dybich.collabdoapp.Snackbar(binding.root,binding.root.context)

        button.setOnClickListener{
            transition.startLoading()
            performKeycloakAction(projectId,taskId)
        }

        return binding.root
    }


    private fun finishTask(accessToken:String, projectId: String, taskId: String){
        taskAPI.changeTaskStatus(accessToken,projectId,taskId,TaskStatus.WaitingForApprovement,isLeader,
            onSuccess = {
                if(projectId!=null){
                    Toast.makeText(binding.root.context,"Task sent to approve", Toast.LENGTH_LONG).show()
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
                finishTask(data.access_token,projectId,taskId)

            },
            onFailure = {error->
                if(error == "Refresh token expired" || error=="Token is not active"){
                    keycloakAPI.getFromEmailAndPass(email!!,password!!,
                        onSuccess = {data ->
                            refreshToken = data.refresh_token
                            finishTask(data.access_token,projectId,taskId)

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