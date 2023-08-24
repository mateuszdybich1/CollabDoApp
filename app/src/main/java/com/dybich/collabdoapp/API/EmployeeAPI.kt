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

object EmployeeAPI {

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
                onFailure(t.message.toString())
            }
        })

    }

}