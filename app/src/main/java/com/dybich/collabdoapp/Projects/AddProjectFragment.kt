package com.dybich.collabdoapp.Projects

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.dybich.collabdoapp.*
import com.dybich.collabdoapp.API.KeycloakAPI
import com.dybich.collabdoapp.API.ProjectAPI
import com.dybich.collabdoapp.Dtos.ProjectDto
import com.dybich.collabdoapp.databinding.FragmentAddProjectBinding


class AddProjectFragment : Fragment() {

    private var email: String? = null
    private var password : String? = null
    private var refreshToken : String? = null

    private val userViewModel: UserViewModel by activityViewModels()

    private lateinit var snackbar : Snackbar

    private lateinit var keycloakAPI : KeycloakAPI
    private lateinit var projectAPI : ProjectAPI

    private lateinit var transition : ButtonTransition


    private lateinit var binding: FragmentAddProjectBinding

    private var priorityChecked : Priority = Priority.Low


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        keycloakAPI = KeycloakAPI()
        projectAPI = ProjectAPI()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        email = userViewModel.email.value
        password = userViewModel.password.value
        refreshToken = userViewModel.refreshToken.value

        binding = FragmentAddProjectBinding.inflate(inflater, container, false)

        transition = ButtonTransition(
            binding.addProjectLayout,
            binding.LoadingCircle,
            binding.addProjectBTN,
            binding.root.context)

        snackbar = com.dybich.collabdoapp.Snackbar(binding.root,binding.root.context)

        binding.addProjectRadioGroup.setOnCheckedChangeListener{group, checkedId ->
            when (checkedId){
                R.id.addProjectLowRadio ->{
                    priorityChecked = Priority.Low
                }
                R.id.addProjectMediumRadio ->{
                    priorityChecked = Priority.Medium
                }
                R.id.addProjectHighRadio ->{
                    priorityChecked = Priority.High
                }
            }
        }

        binding.addProjectBackBTN.setOnClickListener(){
            findNavController().navigateUp()
        }


        binding.addProjectBTN.setOnClickListener{
            if(binding.addProjectET.text!!.length in 1..30){
                transition.startLoading()

                val projectDto : ProjectDto = ProjectDto(null,binding.addProjectET.text.toString(),priorityChecked)
                performKeycloakAction(projectDto)

            }
            else{
                if(binding.addProjectET.text!!.length ==0){
                    binding.addProjectETL.error = "Empty project name"
                }
                else if(binding.addProjectET.text!!.length > 30){
                    binding.addProjectETL.error = "Project name too long"
                }
            }
        }

        return binding.root
    }

    private fun addTask(accessToken:String,projectDto : ProjectDto){
        projectAPI.addProject(accessToken,projectDto,
            onSuccess = {projectId->
             transition.stopLoading()
                Toast.makeText(binding.root.context,"Project created successfully",Toast.LENGTH_LONG).show()
                findNavController().navigateUp()

        }, onFailure = {error->
            snackbar.show(error)
            transition.stopLoading()
        })
    }

    private fun performKeycloakAction(projectDto : ProjectDto){
        keycloakAPI.getFromRefreshToken(refreshToken!!,
            onSuccess = {data ->
                refreshToken = data.refresh_token
                addTask(data.access_token,projectDto)


            },
            onFailure = {error->
                if(error == "Refresh token expired" || error=="Token is not active"){
                    keycloakAPI.getFromEmailAndPass(email!!,password!!,
                        onSuccess = {data ->
                            refreshToken = data.refresh_token
                            addTask(data.access_token,projectDto)
                        },
                        onFailure = {err->
                            snackbar.show(err)
                            transition.stopLoading()
                        })
                }
                else{
                    snackbar.show(error)
                    transition.stopLoading()
                }

            })
    }


}
