package com.dybich.collabdoapp.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.dybich.collabdoapp.API.EmployeeAPI
import com.dybich.collabdoapp.API.KeycloakAPI
import com.dybich.collabdoapp.API.UserAPI
import com.dybich.collabdoapp.LeaderRequestActivity
import com.dybich.collabdoapp.LoggedInActivity
import com.dybich.collabdoapp.databinding.ActivityLoginBinding

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

        val transition = ButtonTransition(binding.loginLayout,binding.BtnTV, binding.LoadingCircle,binding.LoginBTN, this@LoginActivity)

        ClearErrors.clearErrors(listOf(emailErrorObj,passErrorObj))

        binding.LoginBTN.setOnClickListener(){


            email = binding.EmailET.text.toString()
            password = binding.PasswordET.text.toString()

            val emailValidation = Validation.ValidateEmail(email)
            val passwordValidation = Validation.ValidatePassword(password)


            if(emailValidation.IsValid && passwordValidation.IsValid) {

                transition.startLoading()

                UserAPI.verifyEmail(email, onSuccess = { isVerified ->

                    if(isVerified){
                        KeycloakAPI.getFromEmailAndPass(email,password,
                            onSuccess = {keycloakTokenData ->

                                UserAPI.isUserLeader(keycloakTokenData.access_token,
                                    onSuccess = { isLeader ->

                                        if(!isLeader){

                                            EmployeeAPI.getEmployeeDto(keycloakTokenData.access_token,
                                                onSuccess = {employeeDto ->

                                                    if(employeeDto.leaderId=="" || employeeDto.leaderId=="null" || employeeDto.leaderId==null){
                                                        val intent = Intent(this@LoginActivity, LeaderRequestActivity::class.java)
                                                        intent.putExtra("email", email)
                                                        intent.putExtra("password", password)
                                                        intent.putExtra("employeeDto", employeeDto)
                                                        startActivity(intent)

                                                        transition.stopLoading()
                                                    }
                                                    else{
                                                        val intent = Intent(this@LoginActivity, LoggedInActivity::class.java)
                                                        intent.putExtra("email", email)
                                                        intent.putExtra("password", password)
                                                        intent.putExtra("leaderId", employeeDto.leaderId)
                                                        startActivity(intent)

                                                        transition.stopLoading()
                                                    }

                                                },
                                                onFailure = {error->
                                                    Toast.makeText(this,error,Toast.LENGTH_LONG).show()
                                                    transition.stopLoading()
                                                })

                                        }
                                        else{
                                            val intent = Intent(this@LoginActivity, LoggedInActivity::class.java)
                                            intent.putExtra("email", email)
                                            intent.putExtra("password", password)
                                            startActivity(intent)

                                            transition.stopLoading()
                                        }

                                }, onFailure = {error ->
                                    Toast.makeText(this,error,Toast.LENGTH_LONG).show()
                                    transition.stopLoading()
                                })



                        }, onFailure = {error ->
                            Toast.makeText(this,error,Toast.LENGTH_LONG).show()
                            transition.stopLoading()
                        })

                    }
                    else{
                        Toast.makeText(this, "Please verify your email", Toast.LENGTH_LONG).show()
                        transition.stopLoading()
                    }

                }, onFailure = {error ->
                    Toast.makeText(this@LoginActivity,error,Toast.LENGTH_LONG).show()
                    transition.stopLoading()})


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