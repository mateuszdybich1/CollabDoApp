package com.dybich.collabdoapp.IRetrofitAPI

import com.dybich.collabdoapp.Dtos.ProjectDto
import com.dybich.collabdoapp.ProjectStatus
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface IProjectAPI {

    @GET("project")
    fun getinProgressProjectList(@Header("Authorization") token: String,
                                 @Query("leaderId") leaderId : String?,
                                 @Query("projectStatus") projectStatus : ProjectStatus? = ProjectStatus.InProgress,
                                 @Query("pageNumber") pageNumber : Int? = 1) : Call<List<ProjectDto>>

}