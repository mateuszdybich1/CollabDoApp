package com.dybich.collabdoapp.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.dybich.collabdoapp.Keycloak.KeycloakToken
import com.dybich.collabdoapp.databinding.ActivityLoginBinding


import kotlinx.coroutines.*
class LoginActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLoginBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val layout = binding.loginLayout

        binding.LoginBTN.setOnClickListener(){

            val transition = ButtonTransition(layout,binding.BtnTV, binding.LoadingCircle,binding.LoginBTN, this@LoginActivity)

            binding.EmailETL.error = null
            binding.PasswordETL.error = null

            val email = binding.EmailET.text.toString()
            val password = binding.PasswordET.text.toString()

            if(EmailValidation.IsEmailValid(email)) {

                transition.startLoading()

                CoroutineScope(Dispatchers.Main).launch {
                    val tokenData = KeycloakToken.loginResponse(email, password,this@LoginActivity)
                    if (tokenData != null) {
                        Log.d("KEYCLOAK","Access Token: ${tokenData.AccessToken}")
                        println("Refresh Token: ${tokenData.RefreshToken}")
                    }
                }
                transition.stopLoading()
            }
            else {
                binding.EmailETL.error = "Incorrect email"
            }

        }

        binding.ForgotPassTV.setOnClickListener(){}


        binding.SignUpTV.setOnClickListener(){
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

    }




}