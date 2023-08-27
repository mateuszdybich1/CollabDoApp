package com.dybich.collabdoapp.API

import com.dybich.collabdoapp.Dtos.ProjectDto
import com.dybich.collabdoapp.Dtos.UserRegisterDto
import com.dybich.collabdoapp.IRetrofitAPI.IProjectAPI
import com.dybich.collabdoapp.IRetrofitAPI.IUserAPI
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

    fun getProjects(accessToken:String,
                    leaderId:String?,
                    projectStatus : ProjectStatus?,
                    pageNumber : Int?,
                    onSuccess: (List<ProjectDto>) -> Unit,
                    onFailure: (String) -> Unit)
    {

        val call = retrofitAPI.getProjectList("Bearer $accessToken", leaderId, projectStatus, pageNumber)

        call.enqueue(object : Callback<List<ProjectDto>>{

            override fun onResponse(call: Call<List<ProjectDto>>, response: Response<List<ProjectDto>>) {
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
            override fun onFailure(call: Call<List<ProjectDto>>, t: Throwable) {
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