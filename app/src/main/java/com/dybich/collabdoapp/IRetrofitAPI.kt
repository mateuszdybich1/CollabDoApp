package com.dybich.collabdoapp

import com.dybich.collabdoapp.Dtos.UserRegisterDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface IRetrofitAPI {


    @POST("user")
    fun registerUser(@Body userRegisterDto: UserRegisterDto) : Call<String>


    @GET("employee")
    fun getEmployeeDto() : Call<UserRegisterDto>
}