package com.dybich.collabdoapp.API

import com.dybich.collabdoapp.Dtos.EmployeeDto
import com.dybich.collabdoapp.IRetrofitAPI.IEmployeeAPI
import com.dybich.collabdoapp.IRetrofitAPI.IUserAPI
import io.reactivex.internal.operators.single.SingleDoOnSuccess
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class EmployeeAPI () {

    private val baseUrl = "http://192.168.0.110:52000/api/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()


    private val retrofitAPI = retrofit.create(IEmployeeAPI::class.java)

    fun getEmployeeDto(accessToken : String,
                       onSuccess: (EmployeeDto) -> Unit,
                       onFailure : (String) -> Unit)
    {
        val call = retrofitAPI.getEmployeeDto("Bearer $accessToken")

        call.enqueue(object : Callback<EmployeeDto>{
            override fun onResponse(call: Call<EmployeeDto>, response: Response<EmployeeDto>) {
                if (response.isSuccessful) {
                    val employeeDto = response.body()
                    if (employeeDto != null) {
                        onSuccess(employeeDto)
                    } else {
                        onFailure("ERROR")
                    }
                }
                else if(response.code() == 401){
                    onFailure("UNATHORIZED")
                }
                else {
                    val errorBody = response.errorBody()!!.string()
                    onFailure(errorBody)
                }
            }

            override fun onFailure(call: Call<EmployeeDto>, t: Throwable) {
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


    fun createRequest(accessToken : String,
                      leaderEmail : String,
                      onSuccess: (String) -> Unit,
                      onFailure : (String) -> Unit){

        val call = retrofitAPI.createRequest("Bearer $accessToken",leaderEmail)

        call.enqueue(object : Callback<String>{
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    val requestId = response.body()
                    if (requestId != null) {
                        onSuccess(requestId)
                    } else {
                        onFailure("ERROR")
                    }
                }
                else if(response.code() == 401){
                    onFailure("UNATHORIZED")
                }
                else {
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
                      leaderEmail : String,
                      onSuccess: (String) -> Unit,
                      onFailure : (String) -> Unit){

        val call = retrofitAPI.deleteRequest("Bearer $accessToken",leaderEmail)

        call.enqueue(object : Callback<String>{
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    val employeeId = response.body()
                    if (employeeId != null) {
                        onSuccess(employeeId)
                    } else {
                        onFailure("ERROR")
                    }
                }
                else if(response.code() == 401){
                    onFailure("UNATHORIZED")
                }
                else {
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



}