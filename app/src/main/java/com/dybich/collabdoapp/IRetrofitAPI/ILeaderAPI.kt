package com.dybich.collabdoapp.IRetrofitAPI

import com.dybich.collabdoapp.Dtos.EmployeeDto
import com.dybich.collabdoapp.Dtos.EmployeeRequestDto
import retrofit2.Call
import retrofit2.http.*

interface ILeaderAPI {

    @GET("leader/requests")
    fun getEmployeesRequests(@Header("Authorization") token: String) : Call<ArrayList<EmployeeRequestDto>>

    @GET("leader/{leaderId}/email")
    fun getLeaderEmail(@Path("leaderId") leaderId: String) : Call<String>

    @POST("leader/employee/{requestId}")
    fun acceptEmployeeRequest(@Header("Authorization") token: String,
                              @Path("requestId") employeeRequestId: String) : Call<String>

    @DELETE("leader/employee/{requestId}/request")
    fun deleteEmployeeRequest(@Header("Authorization") token: String,
                              @Path("requestId") employeeRequestId: String) : Call<String>

    @DELETE("leader/employee/{employeeId}")
    fun removeEmployeeFromGroup(@Header("Authorization") token: String,
                                @Path("employeeId") employeeId: String) : Call<String>

    @GET("leader/employees")
    fun getEmployeeList(@Header("Authorization") token: String,  @Query("leaderId") leaderId: String?) : Call<ArrayList<EmployeeDto>>
}