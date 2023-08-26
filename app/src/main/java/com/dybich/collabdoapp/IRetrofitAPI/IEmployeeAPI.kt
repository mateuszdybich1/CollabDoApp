package com.dybich.collabdoapp.IRetrofitAPI

import com.dybich.collabdoapp.Dtos.EmployeeDto
import retrofit2.Call
import retrofit2.http.*

interface IEmployeeAPI {
    @GET("employee")
    fun getEmployeeDto(@Header("Authorization") token: String) : Call<EmployeeDto>

    @POST("employee")
    fun createRequest(@Header("Authorization") token: String, @Body leaderEmail : String) : Call<String>

    @DELETE("employee/{leaderEmail}")
    fun deleteRequest(@Header("Authorization") token: String, @Path("leaderEmail") leaderEmail : String) : Call<String>
}