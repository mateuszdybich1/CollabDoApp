package com.dybich.collabdoapp.API

import android.content.Context
import android.widget.Toast
import com.dybich.collabdoapp.Dtos.UserRegisterDto
import com.dybich.collabdoapp.IRetrofitAPI.IUserAPI
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object UserAPI {
    private val baseUrl = "http://192.168.0.110:52000/api/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()


    private val retrofitAPI = retrofit.create(IUserAPI::class.java)
    fun registerUser(userRegisterDto: UserRegisterDto,
                     context: Context,
                     onSuccess: (String) -> Unit){

        val call = retrofitAPI.registerUser(userRegisterDto)

        call.enqueue(object : Callback<String>{
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    val userId = response.body()
                    if (userId != null) {
                        onSuccess(userId)
                    } else {

                        Toast.makeText(context,"Empty GUID",Toast.LENGTH_LONG).show()
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Toast.makeText(context,errorBody,Toast.LENGTH_LONG).show()
                }
            }
            override fun onFailure(call: Call<String>, t: Throwable) {
                Toast.makeText(context,t.message.toString(),Toast.LENGTH_LONG).show()
            }
        })
    }

    fun verifyEmail(email : String,
                    context: Context,
                    onSuccess: (Boolean) -> Unit) {


        val call = retrofitAPI.verifyEmail(email)

        call.enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                if (response.isSuccessful) {
                    val isVerified = response.body()
                    if (isVerified != null) {
                        onSuccess(isVerified)
                    } else {
                        Toast.makeText(context,"ERROR",Toast.LENGTH_LONG).show()
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Toast.makeText(context,errorBody,Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                Toast.makeText(context,t.message.toString(),Toast.LENGTH_LONG).show()
            }

        })
    }

    fun isUserLeader(accessToken : String,
                     context: Context,
                     onSuccess: (Boolean) -> Unit){


        val call = retrofitAPI.isUserLeader(accessToken)

        call.enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                if (response.isSuccessful) {
                    val isLeader = response.body()
                    if (isLeader != null) {
                        onSuccess(isLeader)
                    } else {
                        Toast.makeText(context,"ERROR",Toast.LENGTH_LONG).show()
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Toast.makeText(context,errorBody,Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                Toast.makeText(context,t.message.toString(),Toast.LENGTH_LONG).show()
            }

        })
    }
}