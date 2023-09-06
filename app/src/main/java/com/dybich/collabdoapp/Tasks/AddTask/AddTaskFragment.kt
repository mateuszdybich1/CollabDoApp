package com.dybich.collabdoapp.Tasks.AddTask

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.dybich.collabdoapp.*
import com.dybich.collabdoapp.API.EmployeeAPI
import com.dybich.collabdoapp.API.KeycloakAPI
import com.dybich.collabdoapp.API.LeaderAPI
import com.dybich.collabdoapp.API.TaskAPI
import com.dybich.collabdoapp.Dtos.TaskDto
import com.dybich.collabdoapp.Tasks.TaskViewModel
import com.dybich.collabdoapp.databinding.FragmentAddProjectBinding
import com.dybich.collabdoapp.databinding.FragmentAddTaskBinding
import com.dybich.collabdoapp.databinding.FragmentTaskMainBinding
import com.dybich.collabdoapp.login.ClearErrors
import com.dybich.collabdoapp.login.ErrorObj
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.text.SimpleDateFormat
import java.util.*


class AddTaskFragment : Fragment() {
    private var email: String? = null
    private var password : String? = null
    private var refreshToken : String? = null
    private var isLeader:Boolean?=false
    private var projectId:String?=null

    private val userViewModel: UserViewModel by activityViewModels()
    private val taskViewModel: TaskViewModel by activityViewModels()

    private lateinit var transition : ButtonTransition

    private lateinit var snackbar : Snackbar

    private lateinit var keycloakAPI: KeycloakAPI
    private lateinit var taskAPI: TaskAPI
    private lateinit var leaderAPI: LeaderAPI

    private lateinit var binding: FragmentAddTaskBinding
    private lateinit var backButton : FloatingActionButton

    private lateinit var taskNameETL : TextInputLayout
    private lateinit var taskNameET : TextInputEditText

    private lateinit var taskDescritpionETL : TextInputLayout
    private lateinit var taskDescritpionET : TextInputEditText

    private lateinit var selectDateButton : Button

    private var priorityChecked : Priority = Priority.Low

    private lateinit var dateTextView:TextView

    private var deadlineMilis:Long = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        keycloakAPI = KeycloakAPI()
        taskAPI = TaskAPI()
        leaderAPI = LeaderAPI()


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        email = userViewModel.email.value
        password = userViewModel.password.value
        refreshToken = userViewModel.refreshToken.value
        isLeader = userViewModel.isLeader.value
        projectId = taskViewModel.projectId.value

        binding = FragmentAddTaskBinding.inflate(inflater, container, false)

        transition = ButtonTransition(
            binding.addTaskLayout,
            binding.LoadingCircle,
            binding.addTaskButton,
            binding.root.context)

        snackbar = com.dybich.collabdoapp.Snackbar(binding.root,binding.root.context)

        backButton = binding.fragmentAddTaskBackButton
        selectDateButton = binding.addTaskPickDateBTN

        taskNameETL =binding.addTaskNameETL
        taskNameET = binding.addTaskNameET

        taskDescritpionETL = binding.addTaskDescriptionETL
        taskDescritpionET = binding.addTaskDescriptionET


        val taskNameErrorObj = ErrorObj(taskNameET, taskNameETL)
        val taskDescrErrorObj = ErrorObj(taskDescritpionET, taskDescritpionETL)
        ClearErrors.clearErrors(listOf(taskNameErrorObj,taskDescrErrorObj))

        dateTextView = binding.addTaskDateTV


        binding.addTaskRadioGroup.setOnCheckedChangeListener{group, checkedId ->
            when (checkedId){
                R.id.addTaskLowRadio ->{
                    priorityChecked = Priority.Low
                }
                R.id.addTaskMediumRadio ->{
                    priorityChecked = Priority.Medium
                }
                R.id.addTaskHighRadio ->{
                    priorityChecked = Priority.High
                }
            }
        }

        val selectedDateTime = Calendar.getInstance()
        selectedDateTime.add(Calendar.DAY_OF_YEAR, 1)
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val formattedDate = format.format(selectedDateTime.time)

        dateTextView.text = formattedDate

        selectDateButton.setOnClickListener{
            val datePicker = MaterialDatePicker.Builder.datePicker().build()


            datePicker.addOnPositiveButtonClickListener { dateInMillis ->

                selectedDateTime.timeInMillis = dateInMillis
                val picker =
                    MaterialTimePicker.Builder()
                        .setTimeFormat(TimeFormat.CLOCK_24H)
                        .setHour(12)
                        .setMinute(10).setTitleText("Select deadline time")
                        .build()

                picker.addOnPositiveButtonClickListener {
                    // Ustawiamy wybraną godzinę i minutę w zmiennej selectedDateTime
                    selectedDateTime.set(Calendar.HOUR_OF_DAY, picker.hour)
                    selectedDateTime.set(Calendar.MINUTE, picker.minute)

                    deadlineMilis = selectedDateTime.timeInMillis
                    dateTextView.text = SimpleDateFormat("yyyy-MM-dd HH:mm").format(selectedDateTime.time)

                }
                picker.show(requireActivity().supportFragmentManager, "tag");
            }
            datePicker.show(requireActivity().supportFragmentManager, "datePickerTag")

        }



        backButton.setOnClickListener{
            findNavController().navigateUp()
        }


        binding.addTaskButton.setOnClickListener{
            if(taskNameET.text!!.length in 1..30 &&
                taskDescritpionET.text!!.length in 1..100){

                transition.startLoading()

            }
            else{
                if(taskNameET.text!!.length==0 || taskNameET.text!!.length>30){
                    if(taskNameET.text!!.length==0){
                        taskNameETL.error = "Empty task name"
                    }
                    else{
                        taskNameETL.error = "Task name too long"
                    }
                }
                else{
                    if(taskDescritpionET.text!!.length==0){
                        taskDescritpionETL.error = "Empty description"
                    }
                    else{
                        taskDescritpionETL.error = "Description too long"
                    }
                }
            }
        }
        return binding.root

    }





    private fun addTask(accessToken:String, taskDto: TaskDto){

        taskAPI.addTask(accessToken,taskDto,
        onSuccess = {taskId->
            if (taskId!=null){
                Toast.makeText(binding.root.context,"Task added successfully", Toast.LENGTH_LONG).show()
            }
        },
        onFailure = { error->
            snackbar.show(error)
        })

    }

    private fun getEmployees(accessToken: String,){
        leaderAPI.getEmployeeList(accessToken,null,
            onSuccess = {list->
                 if(list!=null){

                 }
            },
            onFailure = {error->
                snackbar.show(error)
            })
    }

    private fun performKeycloakAction(taskDto: TaskDto?,requestType:String) {
        keycloakAPI.getFromRefreshToken(refreshToken!!,
            onSuccess = { data ->
                refreshToken = data.refresh_token
                if(requestType == "getEmployees"){
                    getEmployees(data.access_token)
                }
                else if(requestType =="addTask"){
                    addTask(data.access_token,taskDto!!)
                }

            },
            onFailure = { error ->
                if (error == "Refresh token expired" || error == "Token is not active") {
                    keycloakAPI.getFromEmailAndPass(email!!, password!!,
                        onSuccess = { data ->
                            refreshToken = data.refresh_token

                        },
                        onFailure = { err ->
                            snackbar.show(err)
                        })
                } else {
                    snackbar.show(error)
                }

            })
    }


}