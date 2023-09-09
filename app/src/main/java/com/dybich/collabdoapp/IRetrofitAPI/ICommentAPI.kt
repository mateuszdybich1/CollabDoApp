package com.dybich.collabdoapp.IRetrofitAPI

import com.dybich.collabdoapp.Dtos.CommentDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ICommentAPI {

    @POST("comment/{taskId}")
    fun addComment(@Header("Authorization") token: String,
                   @Path("taskId") taskId:String,
                   @Body content:String) : Call<String>

    @PUT("comment/{taskId}")
    fun updateComment(@Header("Authorization") token: String,
                      @Path("taskId") taskId:String,
                      @Query("commentId") commentId:String,
                      @Body content:String) : Call<String>


    @DELETE("comment/{taskId}")
    fun deleteComment(@Header("Authorization") token: String,
                      @Path("taskId") taskId:String,
                      @Query("commentId") commentId:String): Call<String>


    @GET("comment/{taskId}/comments")
    fun getComments(@Header("Authorization") token: String,
                    @Path("taskId") taskId:String,
                    @Query("pageNumber") pageNumber: Int,
                    @Query("requestDate") requestDate:Long) : Call<ArrayList<CommentDto>>

    @GET("comment/{taskId}/latest")
    fun getLatestComment(@Header("Authorization") token: String,
                         @Path("taskId") taskId:String,
                         @Query("latestCommentId") latestCommentId:String) : Call<CommentDto>
}