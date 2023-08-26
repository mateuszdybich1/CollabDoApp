package com.dybich.collabdoapp.API

import android.util.Log
import com.dybich.collabdoapp.IRetrofitAPI.IKeycloakAPI
import com.dybich.collabdoapp.Keycloak.KeycloakConfig
import com.dybich.collabdoapp.Keycloak.KeycloakError
import com.dybich.collabdoapp.Keycloak.KeycloakTokenData
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory

class KeycloakAPI () {

    private  val keycloakBaseUrl = KeycloakConfig.ServerAddress
    private  val realm = KeycloakConfig.Realm

    private  val clientId =KeycloakConfig.ClientId
    private  val cientSecret = KeycloakConfig.ClientSecret

    private  val baseUrl = "$keycloakBaseUrl/auth/realms/$realm/protocol/openid-connect/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()


    private val retrofitAPI = retrofit.create(IKeycloakAPI::class.java)



    fun getFromEmailAndPass(email : String,
                            password : String,
                            onSuccess: (KeycloakTokenData) -> Unit,
                            onFailure: (String) -> Unit)

    {

        val call = retrofitAPI.getFromEmailAndPass("password", clientId, cientSecret, email,password)

        call.enqueue(object : Callback<KeycloakTokenData> {
            override fun onResponse(call: Call<KeycloakTokenData>, response: Response<KeycloakTokenData>) {
                if (response.isSuccessful) {
                    val keycloakData = response.body()
                    if (keycloakData != null) {
                        val accessToken = keycloakData.access_token
                        val refreshToken = keycloakData.refresh_token
                        onSuccess(KeycloakTokenData(accessToken, refreshToken))

                    }
                    else {
                        onFailure("TOKEN ERROR")
                    }
                }
                else {
                    val errorBody = response.errorBody()!!
                    try {
                        val keycloakError = retrofit.responseBodyConverter<KeycloakError>(
                            KeycloakError::class.java,
                            arrayOfNulls(0)
                        ).convert(errorBody)
                        if(keycloakError!!.error_description!!.contains("Invalid user credentials")){
                            onFailure("Password or email is incorrect")
                        }
                        else{
                            onFailure(keycloakError!!.error_description!!)
                        }
                    } catch (e: Exception) {
                        onFailure(e.message.toString())
                    }
                }
            }

            override fun onFailure(call: Call<KeycloakTokenData>, t: Throwable) {

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

    fun getFromRefreshToken(refreshToken : String,
                            onSuccess: (KeycloakTokenData) -> Unit,
                            onFailure: (String) -> Unit)

    {

        val call = retrofitAPI.getFromRefreshToken("refresh_token", clientId, cientSecret, refreshToken)

        call.enqueue(object : Callback<KeycloakTokenData> {
            override fun onResponse(call: Call<KeycloakTokenData>, response: Response<KeycloakTokenData>) {
                if (response.isSuccessful) {
                    val keycloakData = response.body()
                    if (keycloakData != null) {
                        val accessToken = keycloakData.access_token
                        val newRefreshToken = keycloakData.refresh_token
                        onSuccess(KeycloakTokenData(accessToken, newRefreshToken))

                    } else {
                        onFailure("TOKEN ERROR")
                    }
                }
                else {
                    val errorBody = response.errorBody()!!
                    try {
                        val keycloakError = retrofit.responseBodyConverter<KeycloakError>(
                            KeycloakError::class.java,
                            arrayOfNulls(0)
                        ).convert(errorBody)
                        onFailure(keycloakError!!.error_description!!)

                    } catch (e: Exception) {
                        onFailure(e.message.toString())
                    }
                }
            }

            override fun onFailure(call: Call<KeycloakTokenData>, t: Throwable) {
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