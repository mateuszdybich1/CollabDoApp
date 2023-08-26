package com.dybich.collabdoapp.IRetrofitAPI

import com.dybich.collabdoapp.Dtos.EmployeeDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface IEmployeeAPI {
    @GET("employee")
    fun getEmployeeDto(@Header("Authorization") token: String) : Call<EmployeeDto>

    @POST("employee")
    fun createRequest(@Header("Authorization") token: String, @Body leaderEmail : String) : Call<String>

    @DELETE("employee")
    fun deleteRequest(@Header("Authorization") token: String, @Body leaderEmail : String) : Call<String>
}