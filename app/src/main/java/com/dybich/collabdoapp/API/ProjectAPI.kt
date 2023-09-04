package com.dybich.collabdoapp.API

import com.dybich.collabdoapp.Dtos.ProjectDto
import com.dybich.collabdoapp.IRetrofitAPI.IProjectAPI
import com.dybich.collabdoapp.ProjectStatus
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ProjectAPI () {
    private val baseUrl = ApiBaseUrl.baseUrl

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()


    private val retrofitAPI = retrofit.create(IProjectAPI::class.java)


    fun addProject(accessToken:String,
                   projectDto: ProjectDto,
                   onSuccess: (String) -> Unit,
                   onFailure: (String) -> Unit){

        val call = retrofitAPI.addProject("Bearer $accessToken",projectDto)

        call.enqueue(object : Callback<String>{

            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    val projectId = response.body()
                    if (projectId != null) {
                        onSuccess(projectId)
                    } else {
                        onFailure("Empty LIST")
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

    fun finishProject(accessToken:String,
                      projectId:String,
                      onSuccess: (String) -> Unit,
                      onFailure: (String) -> Unit){
        val call = retrofitAPI.finishProject("Bearer $accessToken",projectId)

        call.enqueue(object : Callback<String>{

            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    val projectId = response.body()
                    if (projectId != null) {
                        onSuccess(projectId)
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


    fun deleteProject(accessToken:String,
                      projectId:String,
                      onSuccess: (String) -> Unit,
                      onFailure: (String) -> Unit){
        val call = retrofitAPI.deleteProject("Bearer $accessToken",projectId)

        call.enqueue(object : Callback<String>{

            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    val projectId = response.body()
                    if (projectId != null) {
                        onSuccess(projectId)
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


    fun getProjects(accessToken:String,
                    requestDate : Long,
                    leaderId:String?,
                    projectStatus : ProjectStatus?,
                    pageNumber : Int?,
                    onSuccess: (ArrayList<ProjectDto>) -> Unit,
                    onFailure: (String) -> Unit)
    {

        val call = retrofitAPI.getProjectList("Bearer $accessToken",requestDate, leaderId, projectStatus?.value, pageNumber)

        call.enqueue(object : Callback<ArrayList<ProjectDto>>{

            override fun onResponse(call: Call<ArrayList<ProjectDto>>, response: Response<ArrayList<ProjectDto>>) {
                if (response.isSuccessful) {
                    val projectsList = response.body()
                    if (projectsList != null) {
                        onSuccess(projectsList)
                    } else {
                        onFailure("Empty LIST")
                    }
                } else {
                    val errorBody = response.errorBody()!!.string()
                    onFailure(errorBody)
                }
            }
            override fun onFailure(call: Call<ArrayList<ProjectDto>>, t: Throwable) {
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