package com.dybich.collabdoapp.FinishedProjects

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.dybich.collabdoapp.API.KeycloakAPI
import com.dybich.collabdoapp.API.ProjectAPI
import com.dybich.collabdoapp.ButtonTransition
import com.dybich.collabdoapp.ProjectStatus
import com.dybich.collabdoapp.R
import com.dybich.collabdoapp.Snackbar
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class FinishedProjectsBottomSheet : BottomSheetDialogFragment()  {
    companion object {
        fun newInstance(projectId : String, refreshToken: String, email:String,password:String, position:Int) = FinishedProjectsBottomSheet().apply {
            arguments = Bundle().apply {
                putInt("position",position)
                putString("projectId", projectId)
                putString("refreshToken",refreshToken)
                putString("email",email)
                putString("password",password)
            }
        }
    }


    private lateinit var projectId : String
    private lateinit var refreshToken : String
    private lateinit var email : String
    private lateinit var password : String
    private var position : Int = 1

    private lateinit var view:View
    private lateinit var button : Button

    private lateinit var snackbar : Snackbar

    private lateinit var keycloakAPI : KeycloakAPI
    private lateinit var projectAPI : ProjectAPI

    private lateinit var transition : ButtonTransition



    interface OnItemCLickListenerProject {
        fun onItemCLick(position: Int)
    }


    private var listener: OnItemCLickListenerProject? = null


    public fun setOnItemCLickListener(onItemCLickListenerProject: OnItemCLickListenerProject) {
        listener = onItemCLickListenerProject
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        keycloakAPI= KeycloakAPI()
        projectAPI = ProjectAPI()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        view =  inflater.inflate(R.layout.projects_bottom_sheet, container, false)
        transition = ButtonTransition(
            view.findViewById(R.id.projectsBottomSheetLayout),
            view.findViewById(R.id.projectsBottomSheetCircle),
            view.findViewById(R.id.projectBottomSheetButton),
            view.context)

        button = view.findViewById(R.id.projectBottomSheetButton)
        button.text = "Delete project"

        projectId = arguments?.getString("projectId").toString()
        refreshToken = arguments?.getString("refreshToken").toString()
        email = arguments?.getString("email").toString()
        password = arguments?.getString("password").toString()
        position = arguments?.getInt("position")!!

        snackbar = com.dybich.collabdoapp.Snackbar(view,view.context)

        button.setOnClickListener{
            transition.startLoading()
            performKeycloakAction(projectId)
        }

        return view
    }


    private fun deleteProject(accessToken:String, projectId: String){
        projectAPI.deleteProject(accessToken,projectId,
            onSuccess = {projectId->
                transition.stopLoading()
                    if(projectId!=null){
                        Toast.makeText(view.context,"Project deleted", Toast.LENGTH_LONG).show()
                        listener?.onItemCLick(position)
                        dismiss()
                    }
            },
            onFailure = {error->
                snackbar.show(error)
                transition.stopLoading()
                dismiss()
            })
    }

    private fun performKeycloakAction(projectId : String){
        keycloakAPI.getFromRefreshToken(refreshToken!!,
            onSuccess = {data ->
                refreshToken = data.refresh_token
                deleteProject(data.access_token,projectId)

            },
            onFailure = {error->
                if(error == "Refresh token expired" || error=="Token is not active"){
                    keycloakAPI.getFromEmailAndPass(email!!,password!!,
                        onSuccess = {data ->
                            refreshToken = data.refresh_token
                            deleteProject(data.access_token,projectId)

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