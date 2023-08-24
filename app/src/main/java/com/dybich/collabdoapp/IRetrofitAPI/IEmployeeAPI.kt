package com.dybich.collabdoapp.IRetrofitAPI

import com.dybich.collabdoapp.Dtos.EmployeeDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface IEmployeeAPI {
    @GET("employee")
    fun getEmployeeDto(@Header("Authorization") token: String) : Call<EmployeeDto>
}