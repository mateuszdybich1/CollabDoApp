package com.dybich.collabdoapp.API

import com.dybich.collabdoapp.Dtos.EmployeeDto
import com.dybich.collabdoapp.Dtos.EmployeeRequestDto
import com.dybich.collabdoapp.IRetrofitAPI.ILeaderAPI
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LeaderAPI {
    private val baseUrl = ApiBaseUrl.baseUrl

    private val gson: Gson = GsonBuilder()
        .setLenient()
        .create()

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    private val retrofitAPI = retrofit.create(ILeaderAPI::class.java)

    fun getEmployeesRequests(accessToken : String,
                     onSuccess: (ArrayList<EmployeeRequestDto>?) -> Unit,
                     onFailure: (String) -> Unit)
    {
        val call = retrofitAPI.getEmployeesRequests("Bearer $accessToken")

        call.enqueue(object : Callback<ArrayList<EmployeeRequestDto>?> {
            override fun onResponse(call: Call<ArrayList<EmployeeRequestDto>?>, response: Response<ArrayList<EmployeeRequestDto>?>) {
                if (response.isSuccessful) {
                    val list = response.body()
                    onSuccess(list)
                } else {
                    val errorBody = response.errorBody()!!.string()
                    onFailure(errorBody)
                }
            }

            override fun onFailure(call: Call<ArrayList<EmployeeRequestDto>?>, t: Throwable) {
                if(t.message.toString().contains("Failed to connect")){
                    onFailure("No internet connection")
                }
                else if(t.message.toString().contains("failed to connect")){
                    onFailure("Server error")
                }
                else{
                    onFailure(t.message.toString())
                }
            }

        })
    }

    fun getLeaderEmail(leaderId : String,
                       onSuccess: (String) -> Unit,
                       onFailure: (String) -> Unit){

        val call = retrofitAPI.getLeaderEmail(leaderId)

        call.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    val leaderEmail = response.body()
                    if(leaderEmail!=null){
                        onSuccess(leaderEmail)
                    }
                    else{
                        onFailure("ERROR")
                    }
                } else {
                    val errorBody = response.errorBody()!!.string()
                    onFailure(errorBody)
                }
            }
            override fun onFailure(call: Call<String>, t: Throwable) {
                if(t.message.toString().contains("Failed to connect")){
                    onFailure("No internet connection")
                }
                else if(t.message.toString().contains("failed to connect")){
                    onFailure("Server error")
                }
                else{
                    onFailure(t.message.toString())
                }
            }

        })


    }
    fun acceptRequest(accessToken : String,
                      requestId:String,
                      onSuccess: (String) -> Unit,
                      onFailure: (String) -> Unit)
    {
        val call = retrofitAPI.acceptEmployeeRequest("Bearer $accessToken",requestId)

        call.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    val employeeId = response.body()
                    if(employeeId!=null){
                        onSuccess(employeeId)
                    }
                    else{
                        onFailure("ERROR")
                    }
                } else {
                    val errorBody = response.errorBody()!!.string()
                    onFailure(errorBody)
                }
            }
            override fun onFailure(call: Call<String>, t: Throwable) {
                if(t.message.toString().contains("Failed to connect")){
                    onFailure("No internet connection")
                }
                else if(t.message.toString().contains("failed to connect")){
                    onFailure("Server error")
                }
                else{
                    onFailure(t.message.toString())
                }
            }

        })

    }

    fun deleteRequest(accessToken : String,
                      requestId:String,
                      onSuccess: (String) -> Unit,
                      onFailure: (String) -> Unit)
    {
        val call = retrofitAPI.deleteEmployeeRequest("Bearer $accessToken",requestId)

        call.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    val employeeId = response.body()
                    if(employeeId!=null){
                        onSuccess(employeeId)
                    }
                    else{
                        onFailure("ERROR")
                    }
                } else {
                    val errorBody = response.errorBody()!!.string()
                    onFailure(errorBody)
                }
            }
            override fun onFailure(call: Call<String>, t: Throwable) {
                if(t.message.toString().contains("Failed to connect")){
                    onFailure("No internet connection")
                }
                else if(t.message.toString().contains("failed to connect")){
                    onFailure("Server error")
                }
                else{
                    onFailure(t.message.toString())
                }
            }
        })
    }

    fun removeEmployeeFromGroup(accessToken : String,
                      employeeId:String,
                      onSuccess: (String) -> Unit,
                      onFailure: (String) -> Unit)
    {
        val call = retrofitAPI.removeEmployeeFromGroup("Bearer $accessToken",employeeId)

        call.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    val employeeId = response.body()
                    if(employeeId!=null){
                        onSuccess(employeeId)
                    }
                    else{
                        onFailure("ERROR")
                    }
                } else {
                    val errorBody = response.errorBody()!!.string()
                    onFailure(errorBody)
                }
            }
            override fun onFailure(call: Call<String>, t: Throwable) {
                if(t.message.toString().contains("Failed to connect")){
                    onFailure("No internet connection")
                }
                else if(t.message.toString().contains("failed to connect")){
                    onFailure("Server error")
                }
                else{
                    onFailure(t.message.toString())
                }
            }
        })
    }
    fun getEmployeeList(accessToken : String,
                        leaderId: String?,
                        onSuccess: (ArrayList<EmployeeDto>?) -> Unit,
                        onFailure: (String) -> Unit)
    {
        val call = retrofitAPI.getEmployeeList("Bearer $accessToken",leaderId)

        call.enqueue(object : Callback<ArrayList<EmployeeDto>?> {
            override fun onResponse(call: Call<ArrayList<EmployeeDto>?>, response: Response<ArrayList<EmployeeDto>?>) {
                if (response.isSuccessful) {
                    val list = response.body()
                    onSuccess(list)
                } else {
                    val errorBody = response.errorBody()!!.string()
                    onFailure(errorBody)
                }
            }

            override fun onFailure(call: Call<ArrayList<EmployeeDto>?>, t: Throwable) {
                if(t.message.toString().contains("Failed to connect")){
                    onFailure("No internet connection")
                }
                else if(t.message.toString().contains("failed to connect")){
                    onFailure("Server error")
                }
                else{
                    onFailure(t.message.toString())
                }
            }

        })

    }


}