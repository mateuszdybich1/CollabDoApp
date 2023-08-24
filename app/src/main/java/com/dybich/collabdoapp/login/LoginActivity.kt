package com.dybich.collabdoapp.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.dybich.collabdoapp.Keycloak.KeycloakToken
import com.dybich.collabdoapp.API.UserAPI
import com.dybich.collabdoapp.LeaderRequestActivity
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
            val passwordValidation = Validation.ValidatePassword(password)


            if(emailValidation.IsValid && passwordValidation.IsValid) {

                transition.startLoading()

                UserAPI.verifyEmail(email,this@LoginActivity, onSuccess = { isVerified ->

                    if(isVerified){

                        CoroutineScope(Dispatchers.Main).launch {

                            val tokenData = KeycloakToken.getFromEmailAndPass(email, password,this@LoginActivity)

                            if (tokenData != null) {
                                Log.d("KEYCLOAK","Access Token: ${tokenData.AccessToken}")
                                println("Refresh Token: ${tokenData.RefreshToken}")

                                UserAPI.isUserLeader(tokenData.AccessToken,this@LoginActivity,onSuccess = { isLeader ->

                                    Log.d("ISLEADER",isLeader.toString())
                                    if(!isLeader){
                                        val intent = Intent(this@LoginActivity, LeaderRequestActivity::class.java)
                                        startActivity(intent)
                                    }
                                    else{

                                        Toast.makeText(this@LoginActivity,"TODO",Toast.LENGTH_LONG).show()
                                    }
                                    transition.stopLoading()
                                })

                            }
                        }
                    }
                    else{
                        Toast.makeText(this, "Please verify your email", Toast.LENGTH_LONG).show()
                    }
                    transition.stopLoading()
                })

            }
            else {
                if(!emailValidation.IsValid ){
                    binding.EmailETL.error = emailValidation.ErrorMessage
                }
                if(!passwordValidation.IsValid){
                    binding.PasswordETL.error = passwordValidation.ErrorMessage
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