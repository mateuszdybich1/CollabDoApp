package com.dybich.collabdoapp.IRetrofitAPI

import com.dybich.collabdoapp.Dtos.UserRegisterDto
import retrofit2.Call
import retrofit2.http.*

interface IUserAPI {

    @POST("user")
    fun registerUser(@Body userRegisterDto: UserRegisterDto) : Call<String>


    @PUT("user/verify")
    fun verifyEmail(@Body email : String) : Call<Boolean>

    @GET("user")
    fun isUserLeader(@Header("Authorization") token: String) : Call<Boolean>
}