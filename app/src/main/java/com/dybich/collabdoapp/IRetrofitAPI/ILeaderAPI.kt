package com.dybich.collabdoapp.IRetrofitAPI

import com.dybich.collabdoapp.Dtos.EmployeeDto
import com.dybich.collabdoapp.Dtos.EmployeeRequestDto
import retrofit2.Call
import retrofit2.http.*

interface ILeaderAPI {

    @GET("leader/requests")
    fun getEmployeesRequests(@Header("Authorization") token: String) : Call<ArrayList<EmployeeRequestDto>>

    @POST("leader/employee/{requestId}")
    fun acceptEmployeeRequest(@Header("Authorization") token: String,
                              @Path("requestId") employeeRequestId: String) : Call<String>

    @DELETE("leader/employee/{requestId}")
    fun deleteEmployeeRequest(@Header("Authorization") token: String,
                              @Path("requestId") employeeRequestId: String) : Call<String>

    @GET("leader/employees")
    fun getEmployeeList(@Header("Authorization") token: String,  @Query("leaderId") leaderId: String?) : Call<List<EmployeeDto>>
}