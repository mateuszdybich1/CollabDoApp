package com.dybich.collabdoapp

import com.dybich.collabdoapp.Dtos.UserRegisterDto
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type

object RetrofitAPI {
    private val baseUrl = "http://192.168.0.110:52000/api/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()


    fun registerUser(userRegisterDto: UserRegisterDto, onSuccess: (String) -> Unit, onFailure: (Throwable) -> Unit){
        val retrofitAPI = retrofit.create(IRetrofitAPI::class.java)
        val call = retrofitAPI.registerUser(userRegisterDto)

        call.enqueue(object : Callback<String>{
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    val userId = response.body()
                    if (userId != null) {
                        onSuccess(userId)
                    } else {
                        onFailure(Throwable("Empty GUID"))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    onFailure(Throwable(errorBody))
                }
            }
            override fun onFailure(call: Call<String>, t: Throwable) {
                onFailure(Throwable(t.message.toString()))
            }
        })
    }

    fun verifyEmail(email : String, onSuccess: (Boolean) -> Unit, onFailure: (Throwable) -> Unit) {
        val retrofitAPI = retrofit.create(IRetrofitAPI::class.java)
        val call = retrofitAPI.verifyEmail(email)

        call.enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                if (response.isSuccessful) {
                    val isVerified = response.body()
                    if (isVerified != null) {
                        onSuccess(isVerified)
                    } else {
                        onFailure(Throwable("ERROR"))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    onFailure(Throwable(errorBody))
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                onFailure(Throwable(t.message.toString()))
            }

        })
    }
}