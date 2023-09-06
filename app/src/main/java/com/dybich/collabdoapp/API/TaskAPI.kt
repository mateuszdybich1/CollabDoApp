package com.dybich.collabdoapp.API

import com.dybich.collabdoapp.Dtos.TaskDto
import com.dybich.collabdoapp.IRetrofitAPI.ITaskAPI
import com.dybich.collabdoapp.TaskStatus
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Path
import retrofit2.http.Query

class TaskAPI {

    private val baseUrl = ApiBaseUrl.baseUrl

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()


    private val retrofitAPI = retrofit.create(ITaskAPI::class.java)

    fun addTask(accessToken:String,
                taskDto: TaskDto,
                onSuccess: (String) -> Unit,
                onFailure: (String) -> Unit){

        val call = retrofitAPI.addTask("Bearer $accessToken",taskDto)
        call.enqueue(object : Callback<String> {

            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    val taskId = response.body()
                    if (taskId != null) {
                        onSuccess(taskId)
                    } else {
                        onFailure("ERROR")
                    }
                } else {
                    val errorBody = response.errorBody()!!.string()
                    onFailure(errorBody)
                }
            }
            override fun onFailure(call: Call<String>, t: Throwable) {
                if(t.message.toString().contains("Failed to connect")){
                    onFailure("No internet connection")
                }
                else if(t.message.toString().contains("failed to connect")){
                    onFailure("Server error")
                }
                else{
                    onFailure(t.message.toString())
                }
            }
        })
    }

    fun changeTaskStatus(accessToken: String,
                         projectId:String,
                         taskId:String,
                         taskStatus: TaskStatus,
                         isLeader : Boolean,
                         onSuccess: (String) -> Unit,
                         onFailure: (String) -> Unit){

        val call = retrofitAPI.changeTaskStatus("Bearer $accessToken",projectId, taskId, taskStatus, isLeader)

        call.enqueue(object : Callback<String> {

            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    val taskId = response.body()
                    if (taskId != null) {
                        onSuccess(taskId)
                    } else {
                        onFailure("ERROR")
                    }
                } else {
                    val errorBody = response.errorBody()!!.string()
                    onFailure(errorBody)
                }
            }
            override fun onFailure(call: Call<String>, t: Throwable) {
                if(t.message.toString().contains("Failed to connect")){
                    onFailure("No internet connection")
                }
                else if(t.message.toString().contains("failed to connect")){
                    onFailure("Server error")
                }
                else{
                    onFailure(t.message.toString())
                }
            }
        })

    }

    fun deleteTask(accessToken: String,
                   projectId : String,
                   taskId:String,
                   onSuccess: (String) -> Unit,
                   onFailure: (String) -> Unit){

        val call = retrofitAPI.deleteTask("Bearer $accessToken",projectId, taskId)

        call.enqueue(object : Callback<String> {

            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    val taskId = response.body()
                    if (taskId != null) {
                        onSuccess(taskId)
                    } else {
                        onFailure("ERROR")
                    }
                } else {
                    val errorBody = response.errorBody()!!.string()
                    onFailure(errorBody)
                }
            }
            override fun onFailure(call: Call<String>, t: Throwable) {
                if(t.message.toString().contains("Failed to connect")){
                    onFailure("No internet connection")
                }
                else if(t.message.toString().contains("failed to connect")){
                    onFailure("Server error")
                }
                else{
                    onFailure(t.message.toString())
                }
            }
        })

    }

    fun getEmployeeTasks(accessToken: String,
                         projectId:String,
                         requestDate :Long,
                         taskStatus : TaskStatus,
                         pageNumber:Int,
                         onSuccess: (ArrayList<TaskDto>) -> Unit,
                         onFailure: (String) -> Unit){

        val call = retrofitAPI.getEmployeeTasks("Bearer $accessToken",projectId,requestDate,taskStatus,pageNumber)

        call.enqueue(object : Callback<ArrayList<TaskDto>> {

            override fun onResponse(call: Call<ArrayList<TaskDto>>, response: Response<ArrayList<TaskDto>>) {
                if (response.isSuccessful) {
                    val taskList = response.body()
                    if (taskList != null) {
                        onSuccess(taskList)
                    } else {
                        onFailure("Empty LIST")
                    }
                } else {
                    val errorBody = response.errorBody()!!.string()
                    onFailure(errorBody)
                }
            }
            override fun onFailure(call: Call<ArrayList<TaskDto>>, t: Throwable) {
                if(t.message.toString().contains("Failed to connect")){
                    onFailure("No internet connection")
                }
                else if(t.message.toString().contains("failed to connect")){
                    onFailure("Server error")
                }
                else{
                    onFailure(t.message.toString())
                }
            }
        })

    }
    fun getLeaderTasks(accessToken: String,
                       projectId:String,
                       requestDate :Long,
                       taskStatus : TaskStatus,
                       pageNumber:Int,
                       onSuccess: (ArrayList<TaskDto>) -> Unit,
                       onFailure: (String) -> Unit){

        val call = retrofitAPI.getLeaderTasks("Bearer $accessToken",projectId,requestDate,taskStatus,pageNumber)

        call.enqueue(object : Callback<ArrayList<TaskDto>> {

            override fun onResponse(call: Call<ArrayList<TaskDto>>, response: Response<ArrayList<TaskDto>>) {
                if (response.isSuccessful) {
                    val taskList = response.body()
                    if (taskList != null) {
                        onSuccess(taskList)
                    } else {
                        onFailure("Empty LIST")
                    }
                } else {
                    val errorBody = response.errorBody()!!.string()
                    onFailure(errorBody)
                }
            }
            override fun onFailure(call: Call<ArrayList<TaskDto>>, t: Throwable) {
                if(t.message.toString().contains("Failed to connect")){
                    onFailure("No internet connection")
                }
                else if(t.message.toString().contains("failed to connect")){
                    onFailure("Server error")
                }
                else{
                    onFailure(t.message.toString())
                }
            }
        })
    }

    fun getAllTasks(accessToken: String,
                    projectId:String,
                    requestDate :Long,
                    taskStatus : TaskStatus,
                    pageNumber:Int,
                    onSuccess: (ArrayList<TaskDto>) -> Unit,
                    onFailure: (String) -> Unit){


        val call = retrofitAPI.getAllTasks("Bearer $accessToken",projectId,requestDate,taskStatus,pageNumber)

        call.enqueue(object : Callback<ArrayList<TaskDto>> {

            override fun onResponse(call: Call<ArrayList<TaskDto>>, response: Response<ArrayList<TaskDto>>) {
                if (response.isSuccessful) {
                    val taskList = response.body()
                    if (taskList != null) {
                        onSuccess(taskList)
                    } else {
                        onFailure("Empty LIST")
                    }
                } else {
                    val errorBody = response.errorBody()!!.string()
                    onFailure(errorBody)
                }
            }
            override fun onFailure(call: Call<ArrayList<TaskDto>>, t: Throwable) {
                if(t.message.toString().contains("Failed to connect")){
                    onFailure("No internet connection")
                }
                else if(t.message.toString().contains("failed to connect")){
                    onFailure("Server error")
                }
                else{
                    onFailure(t.message.toString())
                }
            }
        })

    }
}