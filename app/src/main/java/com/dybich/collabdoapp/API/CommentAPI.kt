package com.dybich.collabdoapp.API

import com.dybich.collabdoapp.Dtos.CommentDto
import com.dybich.collabdoapp.IRetrofitAPI.ICommentAPI
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CommentAPI {

    private val baseUrl = ApiBaseUrl.baseUrl

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()


    private val retrofitAPI = retrofit.create(ICommentAPI::class.java)


    fun addComment(accessToken:String,
                   taskId: String,
                   content:String,
                   onSuccess: (String) -> Unit,
                   onFailure: (String) -> Unit){

        val call = retrofitAPI.addComment("Bearer $accessToken", taskId,content)
        call.enqueue(object : Callback<String> {

            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    val commentId = response.body()
                    if (commentId != null) {
                        onSuccess(commentId)
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



    fun updateComment(accessToken:String,
                taskId: String,
                commentId:String,
                content:String,
                onSuccess: (String) -> Unit,
                onFailure: (String) -> Unit){

        val call = retrofitAPI.updateComment("Bearer $accessToken", taskId, commentId,content)
        call.enqueue(object : Callback<String> {

            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    val commentId = response.body()
                    if (commentId != null) {
                        onSuccess(commentId)
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



    fun deleteComment(accessToken:String,
                      taskId: String,
                      commentId:String,
                      onSuccess: (String) -> Unit,
                      onFailure: (String) -> Unit){

        val call = retrofitAPI.deleteComment("Bearer $accessToken", taskId, commentId)
        call.enqueue(object : Callback<String> {

            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    val commentId = response.body()
                    if (commentId != null) {
                        onSuccess(commentId)
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



    fun getComments(accessToken:String,
                      taskId: String,
                      pageNumber:Int,
                      requestDate:Long,
                      onSuccess: (ArrayList<CommentDto>) -> Unit,
                      onFailure: (String) -> Unit){

        val call = retrofitAPI.getComments("Bearer $accessToken", taskId, pageNumber,requestDate)
        call.enqueue(object : Callback<ArrayList<CommentDto>> {

            override fun onResponse(call: Call<ArrayList<CommentDto>>, response: Response<ArrayList<CommentDto>>) {
                if (response.isSuccessful) {
                    val comments = response.body()
                    if (comments != null) {
                        onSuccess(comments)
                    } else {
                        onFailure("ERROR")
                    }
                } else {
                    val errorBody = response.errorBody()!!.string()
                    onFailure(errorBody)
                }
            }
            override fun onFailure(call: Call<ArrayList<CommentDto>>, t: Throwable) {
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


    fun getLatestComment(accessToken:String,
                    taskId: String,
                    latestCommentId:String,
                    onSuccess: (CommentDto) -> Unit,
                    onFailure: (String) -> Unit){

        val call = retrofitAPI.getLatestComment("Bearer $accessToken", taskId,latestCommentId)
        call.enqueue(object : Callback<CommentDto> {

            override fun onResponse(call: Call<CommentDto>, response: Response<CommentDto>) {
                if (response.isSuccessful) {
                    val newComment = response.body()
                    if (newComment != null) {
                        onSuccess(newComment)
                    } else {
                        onFailure("ERROR")
                    }
                }
                else if(response.code() == 404){
                    onFailure("404")
                }
                else {
                    val errorBody = response.errorBody()!!.string()
                    onFailure(errorBody)
                }
            }
            override fun onFailure(call: Call<CommentDto>, t: Throwable) {
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