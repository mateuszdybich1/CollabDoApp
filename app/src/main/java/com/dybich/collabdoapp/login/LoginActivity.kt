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

    private lateinit var email : String
    private lateinit var password : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val emailErrorObj = ErrorObj(binding.EmailET, binding.EmailETL)
        val passErrorObj = ErrorObj(binding.PasswordET, binding.PasswordETL)

        ClearErrors.clearErrors(listOf(emailErrorObj,passErrorObj))

        binding.LoginBTN.setOnClickListener(){

            val transition = ButtonTransition(binding.loginLayout,binding.BtnTV, binding.LoadingCircle,binding.LoginBTN, this@LoginActivity)



            email = binding.EmailET.text.toString()
            password = binding.PasswordET.text.toString()

            val emailValidation = Validation.ValidateEmail(email)
            val passwrodValidation = Validation.ValidatePassword(password)



            if(emailValidation.IsValid && passwrodValidation.IsValid) {

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
                if(!emailValidation.IsValid ){
                    binding.EmailETL.error = emailValidation.ErrorMessage
                }
                if(!passwrodValidation.IsValid){
                    binding.PasswordETL.error = passwrodValidation.ErrorMessage
                }

            }

        }

        binding.ForgotPassTV.setOnClickListener(){}


        binding.SignUpTV.setOnClickListener(){
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

    }




}