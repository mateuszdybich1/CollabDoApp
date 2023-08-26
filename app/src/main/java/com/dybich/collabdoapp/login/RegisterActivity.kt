package com.dybich.collabdoapp.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.dybich.collabdoapp.API.KeycloakAPI
import com.dybich.collabdoapp.Dtos.UserRegisterDto
import com.dybich.collabdoapp.API.UserAPI
import com.dybich.collabdoapp.ButtonTransition
import com.dybich.collabdoapp.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding : ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val transition = ButtonTransition(
            binding.registerLayout,
            binding.RegisterBTN,
            binding.RegisterPB,
            binding.RegisterBTN,
            this@RegisterActivity)

        val usernameErrorObj = ErrorObj(binding.NicknameET, binding.NicknameETL)
        val emailErrorObj = ErrorObj(binding.RegEmailET, binding.RegEmailETL)
        val passErrorObj = ErrorObj(binding.RegPasswordET, binding.RegPasswordETL)
        val repPassErrorObj = ErrorObj(binding.RepPassET, binding.RepPassETL)

        ClearErrors.clearErrors(listOf(usernameErrorObj,emailErrorObj,passErrorObj,repPassErrorObj))


        val userAPI = UserAPI()
        val keycloakAPI = KeycloakAPI()

        binding.RegisterBTN.setOnClickListener(){


            val username = binding.NicknameET.text.toString()
            val email = binding.RegEmailET.text.toString()
            val password = binding.RegPasswordET.text.toString()
            val repeatPass = binding.RepPassET.text.toString()
            val isLeader = binding.registerS.isChecked

            val usernameValidation = Validation.ValidateUsername(username)
            val emailValidation = Validation.ValidateEmail(email)
            val passwordValidation = Validation.ValidatePassword(password)
            val repeatPassValidation = Validation.ValidateRepeatPassword(password,repeatPass)

            if(usernameValidation.IsValid &&
                emailValidation.IsValid &&
                passwordValidation.IsValid &&
                repeatPassValidation.IsValid){

                transition.startLoading()

                val registerObj = UserRegisterDto(username,email,password,isLeader)

                userAPI.registerUser(registerObj,
                    onSuccess = { userId ->

                        keycloakAPI.getFromEmailAndPass(email,password,
                            onSuccess = { keycloakTokenData ->

                                userAPI.verifyEmail(keycloakTokenData.access_token,
                                    onSuccess = { isVerified ->

                                        if(!isVerified){
                                            Toast.makeText(this, "Success! Sent verification email",Toast.LENGTH_LONG).show()
                                            val intent = Intent(this, LoginActivity::class.java)
                                            startActivity(intent)
                                        }
                                        transition.stopLoading()
                                    }, onFailure = { error->
                                        Toast.makeText(this@RegisterActivity, error,Toast.LENGTH_LONG).show()

                                        transition.stopLoading()})

                            },
                            onFailure = {error->
                                Toast.makeText(this@RegisterActivity, error,Toast.LENGTH_LONG).show()

                                transition.stopLoading()
                            } )


                    }, onFailure = { error->
                        Toast.makeText(this@RegisterActivity, error,Toast.LENGTH_LONG).show()

                        transition.stopLoading()})

            }
            else{
                if(!usernameValidation.IsValid){
                    binding.NicknameETL.error = usernameValidation.ErrorMessage
                }
                if(!emailValidation.IsValid){
                    binding.RegEmailETL.error = emailValidation.ErrorMessage
                }
                if(!passwordValidation.IsValid){
                    binding.RegPasswordETL.error = passwordValidation.ErrorMessage
                }
                if(!repeatPassValidation.IsValid){
                    binding.RepPassETL.error = repeatPassValidation.ErrorMessage
                }
            }
        }


    }
}