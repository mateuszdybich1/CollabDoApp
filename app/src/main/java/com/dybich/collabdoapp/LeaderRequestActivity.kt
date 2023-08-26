package com.dybich.collabdoapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.dybich.collabdoapp.API.EmployeeAPI
import com.dybich.collabdoapp.API.KeycloakAPI
import com.dybich.collabdoapp.API.UserAPI
import com.dybich.collabdoapp.Dtos.EmployeeDto
import com.dybich.collabdoapp.Keycloak.KeycloakTokenData
import com.dybich.collabdoapp.databinding.ActivityLeaderRequestBinding
import com.dybich.collabdoapp.login.Validation

class LeaderRequestActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLeaderRequestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLeaderRequestBinding.inflate(layoutInflater)
        setContentView(binding.root)



        val email: String? = intent.getStringExtra("email")
        val password : String? = intent.getStringExtra("password")
        var refreshToken : String? = intent.getStringExtra("refreshToken")

        val employeeDto : EmployeeDto? = intent.getParcelableExtra("employeeDto")



        val transition = ButtonTransition(
            binding.leaderRequestLayout,
            binding.BtnTV,
            binding.LoadingCircle,
            binding.SendRequest,
            this@LeaderRequestActivity)


        if(employeeDto?.leaderRequestEmail !="" && employeeDto?.leaderRequestEmail != null ){
            updateLayout(binding, employeeDto.leaderRequestEmail)
        }


        val employeeAPI = EmployeeAPI()
        val keycloakAPI = KeycloakAPI()


        binding.SendRequest.setOnClickListener(){

            val emailValidation = Validation.ValidateEmail(binding.EmailET.text.toString())

            if(emailValidation.IsValid){
                transition.startLoading()



                if(employeeDto?.leaderRequestEmail !="" && employeeDto?.leaderRequestEmail != null ){


                    keycloakAPI.getFromRefreshToken(refreshToken!!,
                        onSuccess = {data ->
                            refreshToken = data.refresh_token

                            employeeAPI.createRequest(data.access_token, binding.EmailET.text.toString(),
                                onSuccess = {requestId ->
                                    Toast.makeText(this@LeaderRequestActivity, "Request created",Toast.LENGTH_LONG).show()
                                    updateLayout(binding, binding.EmailET.text.toString())
                                    employeeDto?.leaderId = binding.EmailET.text.toString()
                                    transition.stopLoading()
                                },
                                onFailure = {error->
                                    Toast.makeText(this@LeaderRequestActivity, error,Toast.LENGTH_LONG).show()
                                    transition.stopLoading()
                                })
                        },
                        onFailure = {error->
                            if(error == "Refresh token expired"){
                                keycloakAPI.getFromEmailAndPass(email!!,password!!,
                                    onSuccess = {data ->

                                        refreshToken = data.refresh_token

                                        employeeAPI.createRequest(data.access_token, binding.EmailET.text.toString(),
                                            onSuccess = {requestId ->
                                                Toast.makeText(this@LeaderRequestActivity, "Request created",Toast.LENGTH_LONG).show()
                                                updateLayout(binding, binding.EmailET.text.toString())
                                                employeeDto?.leaderId = binding.EmailET.text.toString()
                                                transition.stopLoading()
                                            },
                                            onFailure = {error->
                                                Toast.makeText(this@LeaderRequestActivity, error,Toast.LENGTH_LONG).show()
                                                transition.stopLoading()
                                            })

                                    },
                                    onFailure = {err->
                                        Toast.makeText(this@LeaderRequestActivity, err,Toast.LENGTH_LONG).show()
                                        transition.stopLoading()
                                    })
                            }
                            else{
                                Toast.makeText(this@LeaderRequestActivity, error,Toast.LENGTH_LONG).show()
                                transition.stopLoading()
                            }

                        })


                }
                else{

                    keycloakAPI.getFromRefreshToken(refreshToken!!,
                        onSuccess = {data ->
                            refreshToken = data.refresh_token

                            employeeAPI.createRequest(data.access_token, binding.EmailET.text.toString(),
                                onSuccess = {requestId ->
                                    Toast.makeText(this@LeaderRequestActivity, "Request created",Toast.LENGTH_LONG).show()
                                    updateLayout(binding, binding.EmailET.text.toString())
                                    employeeDto?.leaderId = binding.EmailET.text.toString()
                                    transition.stopLoading()
                                },
                                onFailure = {error->
                                    Toast.makeText(this@LeaderRequestActivity, error,Toast.LENGTH_LONG).show()
                                    transition.stopLoading()
                                })
                        },
                        onFailure = {error->
                            if(error == "Refresh token expired"){
                                keycloakAPI.getFromEmailAndPass(email!!,password!!,
                                    onSuccess = {data ->

                                        refreshToken = data.refresh_token

                                        employeeAPI.createRequest(data.access_token, binding.EmailET.text.toString(),
                                            onSuccess = {requestId ->
                                                Toast.makeText(this@LeaderRequestActivity, "Request created",Toast.LENGTH_LONG).show()
                                                updateLayout(binding, binding.EmailET.text.toString())
                                                employeeDto?.leaderId = binding.EmailET.text.toString()
                                                transition.stopLoading()
                                            },
                                            onFailure = {error->
                                                Toast.makeText(this@LeaderRequestActivity, error,Toast.LENGTH_LONG).show()
                                                transition.stopLoading()
                                            })

                                    },
                                    onFailure = {err->
                                        Toast.makeText(this@LeaderRequestActivity, err,Toast.LENGTH_LONG).show()
                                        transition.stopLoading()
                                    })
                            }
                            else{
                                Toast.makeText(this@LeaderRequestActivity, error,Toast.LENGTH_LONG).show()
                                transition.stopLoading()
                            }

                        })
                }
            }
            else{
                binding.EmailETL.error = emailValidation.ErrorMessage
            }

        }

    }
    private fun updateLayout( binding: ActivityLeaderRequestBinding, leaderEmail :String){
        binding.EmailETL.isEnabled = false
        binding.EmailETL.isHelperTextEnabled = true
        binding.EmailETL.helperText = "Waiting for approve"
        binding.EmailET.setText(leaderEmail)
        binding.BtnTV.text = "Cancel request"
    }

    private var doubleBackToExitPressedOnce = false
    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finishAffinity()
            finish()
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "click BACK again to exit", Toast.LENGTH_SHORT).show()

        Handler(Looper.getMainLooper()).postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)
    }
}