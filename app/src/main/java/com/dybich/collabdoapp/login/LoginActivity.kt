package com.dybich.collabdoapp.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dybich.collabdoapp.API.EmployeeAPI
import com.dybich.collabdoapp.API.KeycloakAPI
import com.dybich.collabdoapp.API.UserAPI
import com.dybich.collabdoapp.ButtonTransition
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

        ClearErrors.clearErrors(listOf(emailErrorObj,passErrorObj))

        val transition = ButtonTransition(binding.loginLayout,binding.LoadingCircle,binding.LoginBTN, this@LoginActivity)

        val snackbar : com.dybich.collabdoapp.Snackbar = com.dybich.collabdoapp.Snackbar(binding.root,this@LoginActivity)

        val employeeAPI = EmployeeAPI()
        val keycloakAPI = KeycloakAPI()
        val userAPI = UserAPI()

        binding.LoginBTN.setOnClickListener(){


            email = binding.EmailET.text.toString()
            password = binding.PasswordET.text.toString()

            val emailValidation = Validation.ValidateEmail(email)
            val passwordValidation = Validation.ValidatePassword(password)


            if(emailValidation.IsValid && passwordValidation.IsValid) {

                transition.startLoading()

                keycloakAPI.getFromEmailAndPass(email,password,
                    onSuccess = {keycloakTokenData ->

                        userAPI.verifyEmail(keycloakTokenData.access_token,
                            onSuccess = { isVerified ->

                                if(isVerified){

                                    userAPI.isUserLeader(keycloakTokenData.access_token,
                                        onSuccess = { isLeader ->

                                            if(!isLeader){

                                                employeeAPI.getEmployeeDto(keycloakTokenData.access_token,
                                                    onSuccess = {employeeDto ->

                                                        if(employeeDto.leaderId=="" || employeeDto.leaderId=="null" || employeeDto.leaderId==null){
                                                            val intent = Intent(this@LoginActivity, LeaderRequestActivity::class.java)
                                                            intent.putExtra("email", email)
                                                            intent.putExtra("password", password)
                                                            intent.putExtra("employeeDto", employeeDto)
                                                            intent.putExtra("refreshToken", keycloakTokenData.refresh_token)
                                                            startActivity(intent)
                                                            finish()
                                                            transition.stopLoading()
                                                        }
                                                        else{
                                                            val intent = Intent(this@LoginActivity, LoggedInActivity::class.java)
                                                            intent.putExtra("email", email)
                                                            intent.putExtra("password", password)
                                                            intent.putExtra("isLeader", false)
                                                            intent.putExtra("leaderId", employeeDto.leaderId)
                                                            intent.putExtra("refreshToken", keycloakTokenData.refresh_token)
                                                            startActivity(intent)
                                                            finish()
                                                            transition.stopLoading()
                                                        }

                                                    },
                                                    onFailure = {error->
                                                        snackbar.show(error)
                                                        transition.stopLoading()
                                                    })

                                            }
                                            else{
                                                val intent = Intent(this@LoginActivity, LoggedInActivity::class.java)
                                                intent.putExtra("email", email)
                                                intent.putExtra("password", password)
                                                intent.putExtra("isLeader", true)
                                                intent.putExtra("refreshToken", keycloakTokenData.refresh_token)
                                                startActivity(intent)
                                                finish()
                                                transition.stopLoading()
                                            }

                                        }, onFailure = {error ->
                                            snackbar.show(error)
                                            transition.stopLoading()
                                        })


                                }
                                else{
                                    snackbar.show("Please verify your email")
                                    transition.stopLoading()
                                }

                            }, onFailure = {error ->
                                snackbar.show(error)
                            transition.stopLoading()})

                    }, onFailure = {error ->
                        snackbar.show(error)
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

        binding.ForgotPassTV.setOnClickListener(){
            val intent = Intent(this, ResetPasswordActivity::class.java)
            startActivity(intent)
        }


        binding.SignUpTV.setOnClickListener(){
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}