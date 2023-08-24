package com.dybich.collabdoapp.IRetrofitAPI

import com.dybich.collabdoapp.Dtos.UserRegisterDto
import com.dybich.collabdoapp.Keycloak.KeycloakTokenData
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface IKeycloakAPI {

    @POST("token")
    @FormUrlEncoded
    fun getFromEmailAndPass(@Field("grant_type") grantType: String,
                            @Field("client_id") clientId: String,
                            @Field("client_secret") clientSecret: String,
                            @Field("username") email: String,
                            @Field("password") password: String) : Call<KeycloakTokenData>


    @POST("token")
    @FormUrlEncoded
    fun getFromRefreshToken(@Field("grant_type") grantType: String,
                            @Field("client_id") clientId: String,
                            @Field("client_secret") clientSecret: String,
                            @Field("refresh_token") refreshToken: String) : Call<KeycloakTokenData>


}