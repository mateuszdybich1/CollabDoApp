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

object UserAPI {
    private val baseUrl = "http://192.168.0.110:52000/api/"

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
                onFailure(t.message.toString())
            }
        })
    }

    fun verifyEmail(email : String,
                    onSuccess: (Boolean) -> Unit,
                    onFailure: (String) -> Unit) {


        val call = retrofitAPI.verifyEmail(email)

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
                onFailure(t.message.toString())
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
                 onFailure(t.message.toString())
             }
         })
    }
}