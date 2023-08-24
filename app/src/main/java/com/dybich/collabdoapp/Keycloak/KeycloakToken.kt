package com.dybich.collabdoapp.Keycloak

import android.content.Context
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException

object KeycloakToken {

    suspend fun getFromEmailAndPass(email: String, password: String, context: Context): KeycloakTokenData? {
            return withContext(Dispatchers.IO) {
                try {
                    val keycloakBaseUrl = KeycloakConfig.ServerAddress
                    val realm = KeycloakConfig.Realm

                    val tokenUrl = "$keycloakBaseUrl/auth/realms/$realm/protocol/openid-connect/token"

                    val client = OkHttpClient()

                    val formBody = FormBody.Builder()
                        .add("grant_type", "password")
                        .add("client_id", KeycloakConfig.ClientId)
                        .add("client_secret", KeycloakConfig.ClientSecret)
                        .add("username", email)
                        .add("password", password)
                        .build()

                    val request = Request.Builder()
                        .url(tokenUrl)
                        .post(formBody)
                        .build()

                    val response = client.newCall(request).execute()

                    try {
                        if (response.isSuccessful) {
                            val jsonResponse = JSONObject(response.body?.string())
                            val accessToken = jsonResponse.getString("access_token")
                            val refreshToken = jsonResponse.getString("refresh_token")
                            KeycloakTokenData(accessToken, refreshToken)
                        } else {
                            if(response.code == 401){
                                withContext(Dispatchers.Main) {
                                    Log.d("TAG", response.message)
                                    Toast.makeText(context, "Email or password is incorrect", Toast.LENGTH_LONG).show()
                                }
                            }
                            else{
                                withContext(Dispatchers.Main) {
                                    Log.d("TAG", response.message)
                                    Toast.makeText(context, "Error obtaining Keycloak token: ${response.message}", Toast.LENGTH_LONG).show()
                                }
                            }
                            null
                        }
                    } finally {
                        response.body?.close()
                    }

                } catch (e: IOException) {
                    withContext(Dispatchers.Main) {
                        Log.d("TAG", e.message ?: "Unknown error")
                        Toast.makeText(context, "Error obtaining Keycloak token: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                    null
                }
            }
    }

    suspend fun GetFromRefreshToken(refreshToken : String, context: Context): KeycloakTokenData?{
        return withContext(Dispatchers.IO) {
            try {
                val keycloakBaseUrl = KeycloakConfig.ServerAddress
                val realm = KeycloakConfig.Realm

                val tokenUrl = "$keycloakBaseUrl/auth/realms/$realm/protocol/openid-connect/token"

                val client = OkHttpClient()

                val formBody = FormBody.Builder()
                    .add("grant_type", "refresh_token")
                    .add("client_id", KeycloakConfig.ClientId)
                    .add("client_secret", KeycloakConfig.ClientSecret)
                    .add("refresh_token", refreshToken)
                    .build()

                val request = Request.Builder()
                    .url(tokenUrl)
                    .post(formBody)
                    .build()

                val response = client.newCall(request).execute()

                try {
                    if (response.isSuccessful) {
                        val jsonResponse = JSONObject(response.body?.string())
                        val accessToken = jsonResponse.getString("access_token")
                        val newRefreshToken = jsonResponse.getString("refresh_token")
                        KeycloakTokenData(accessToken, newRefreshToken)
                    } else {
                        withContext(Dispatchers.Main) {
                            Log.d("TAG", response.message)
                            Toast.makeText(
                                context,
                                "Error obtaining Keycloak token: ${response.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        null
                    }
                } finally {
                    response.body?.close()
                }
            } catch (e: IOException) {

                withContext(Dispatchers.Main) {
                    Log.d("TAG", e.message ?: "Unknown error")
                    Toast.makeText(context, "Error obtaining Keycloak token: ${e.message}", Toast.LENGTH_LONG).show()
                }
                null
            }
        }
    }
}