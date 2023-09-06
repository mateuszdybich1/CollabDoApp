package com.dybich.collabdoapp.IRetrofitAPI

import com.dybich.collabdoapp.Dtos.ProjectDto
import com.dybich.collabdoapp.ProjectStatus
import retrofit2.Call
import retrofit2.http.*

interface IProjectAPI {

    @POST("project")
    fun addProject(@Header("Authorization") token: String,
                   @Body projectDto: ProjectDto) : Call<String>

    @PUT("project/{projectId}")
    fun finishProject(@Header("Authorization") token: String,
                      @Path("projectId") projectId : String) : Call<String>

    @DELETE("project/{projectId}")
    fun deleteProject(@Header("Authorization") token: String,
                      @Path("projectId") projectId : String) : Call<String>

    @GET("project")
    fun getProjectList(@Header("Authorization") token: String,
                       @Query("requestDate") requestDate : Long,
                       @Query("leaderId") leaderId : String?,
                       @Query("projectStatus") projectStatus : Int? = ProjectStatus.InProgress.value,
                       @Query("pageNumber") pageNumber : Int? = 1) : Call<ArrayList<ProjectDto>>

}