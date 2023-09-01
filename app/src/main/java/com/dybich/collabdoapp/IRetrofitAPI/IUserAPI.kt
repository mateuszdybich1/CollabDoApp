package com.dybich.collabdoapp.IRetrofitAPI

import com.dybich.collabdoapp.Dtos.UserDto
import com.dybich.collabdoapp.Dtos.UserRegisterDto
import retrofit2.Call
import retrofit2.http.*

interface IUserAPI {

    @POST("user")
    fun registerUser(@Body userRegisterDto: UserRegisterDto) : Call<String>


    @PUT("user/verify")
    fun verifyEmail(@Header("Authorization") token: String) : Call<Boolean>

    @PUT("user/password")
    fun resetPassword(@Body userEmail: String) : Call<Boolean>

    @GET("user/isleader")
    fun isUserLeader(@Header("Authorization") token: String) : Call<Boolean>

    @GET("user")
    fun getUser(@Header("Authorization") token: String) : Call<UserDto>



}