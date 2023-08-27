package com.dybich.collabdoapp.API

import android.content.Context
import android.hardware.camera2.CaptureFailure
import android.widget.Toast
import com.dybich.collabdoapp.Dtos.UserRegisterDto
import com.dybich.collabdoapp.IRetrofitAPI.IUserAPI
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class UserAPI () {
    private val baseUrl = ApiBaseUrl.baseUrl

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()


    private val retrofitAPI = retrofit.create(IUserAPI::class.java)
    fun registerUser(userRegisterDto: UserRegisterDto,
                     onSuccess: (String) -> Unit,
                     onFailure: (String) -> Unit){

        val call = retrofitAPI.registerUser(userRegisterDto)

        call.enqueue(object : Callback<String>{
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    val userId = response.body()
                    if (userId != null) {
                        onSuccess(userId)
                    } else {
                        onFailure("Empty GUID")
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

    fun verifyEmail(accessToken : String,
                    onSuccess: (Boolean) -> Unit,
                    onFailure: (String) -> Unit) {


        val call = retrofitAPI.verifyEmail("Bearer $accessToken")

        call.enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                if (response.isSuccessful) {
                    val isVerified = response.body()
                    if (isVerified != null) {
                        onSuccess(isVerified)
                    } else {
                        onFailure("ERROR")
                    }
                } else {
                    val errorBody = response.errorBody()!!.string()
                    onFailure(errorBody)
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
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

    fun resetPassword(userEmail : String,
                    onSuccess: (String) -> Unit,
                    onFailure: (String) -> Unit) {


        val call = retrofitAPI.resetPassword(userEmail)

        call.enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                if (response.isSuccessful) {
                    val message = response.body()
                    if (message != null) {
                        onSuccess("Sent successfully")
                    } else {
                        onFailure("ERROR")
                    }
                } else {
                    val errorBody = response.errorBody()!!.string()
                    onFailure(errorBody)
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
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

     fun isUserLeader(accessToken: String,
                      onSuccess: (Boolean) -> Unit,
                      onFailure: (String) -> Unit) {


         val call = retrofitAPI.isUserLeader("Bearer $accessToken" )

         call.enqueue(object : Callback<Boolean> {
             override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                 if (response.isSuccessful) {
                     val isLeader = response.body()
                     if (isLeader != null) {
                         onSuccess(isLeader)
                     } else {
                         onFailure("ERROR")
                     }
                 }
                 else if(response.code() == 401){
                     onFailure("UNATHORIZED")
                 }
                 else {
                     val errorBody = response.errorBody()!!.string()
                     onFailure(errorBody)
                 }
             }

             override fun onFailure(call: Call<Boolean>, t: Throwable) {
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