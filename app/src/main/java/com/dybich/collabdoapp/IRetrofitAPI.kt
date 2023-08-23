package com.dybich.collabdoapp

import com.dybich.collabdoapp.Dtos.UserRegisterDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

interface IRetrofitAPI {


    @POST("user")
    fun registerUser(@Body userRegisterDto: UserRegisterDto) : Call<String>


    @PUT("user/verify")
    fun verifyEmail(@Body email : String) : Call<Boolean>

    @GET("employee")
    fun getEmployeeDto() : Call<UserRegisterDto>
}