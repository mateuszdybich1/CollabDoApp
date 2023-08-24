package com.dybich.collabdoapp.API

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object EmployeeAPI {

    private val baseUrl = "http://192.168.0.110:52000/api/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()


}