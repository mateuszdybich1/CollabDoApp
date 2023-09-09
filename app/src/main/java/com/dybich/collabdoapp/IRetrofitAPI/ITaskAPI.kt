package com.dybich.collabdoapp.IRetrofitAPI

import com.dybich.collabdoapp.Dtos.TaskDto
import com.dybich.collabdoapp.TaskStatus
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ITaskAPI {

    @POST("task")
    fun addTask(@Header("Authorization") token: String,
                @Body taskDto: TaskDto) : Call<String>


    @PUT("task/{projectId}/{taskId}")
    fun changeTaskStatus(@Header("Authorization") token: String,
                         @Path("projectId") projectId:String,
                         @Path("taskId") taskId:String,
                         @Query("taskStatus") taskStatus: TaskStatus,
                         @Query("isLeader") isLeader : Boolean) : Call<String>

    @DELETE("task/{projectId}/{taskId}")
    fun deleteTask(@Header("Authorization") token: String,
                   @Path("projectId") projectId : String,
                   @Path("taskId")taskId:String) : Call<String>



    @GET("task/user/{projectId}")
    fun getUserTasks(@Header("Authorization") token: String,
                     @Path("projectId") projectId:String,
                     @Query("requestDate") requestDate :Long,
                     @Query("taskStatus") taskStatus : TaskStatus,
                     @Query("pageNumber") pageNumber:Int) : Call<ArrayList<TaskDto>>

    @GET("task/all/{projectId}")
    fun getAllTasks(@Header("Authorization") token: String,
                    @Path("projectId") projectId:String,
                    @Query("requestDate") requestDate :Long,
                    @Query("taskStatus") taskStatus : TaskStatus,
                    @Query("pageNumber") pageNumber:Int) : Call<ArrayList<TaskDto>>


}