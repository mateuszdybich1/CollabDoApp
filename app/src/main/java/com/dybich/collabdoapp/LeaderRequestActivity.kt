package com.dybich.collabdoapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.dybich.collabdoapp.API.EmployeeAPI
import com.dybich.collabdoapp.API.KeycloakAPI
import com.dybich.collabdoapp.Dtos.EmployeeDto
import com.dybich.collabdoapp.databinding.ActivityLeaderRequestBinding
import com.dybich.collabdoapp.login.ClearErrors
import com.dybich.collabdoapp.login.ErrorObj
import com.dybich.collabdoapp.login.Validation

class LeaderRequestActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLeaderRequestBinding

    private var email : String? = null
    private var password : String? = null
    private var refreshToken : String? = null
    private  var employeeDto : EmployeeDto? = null

    private val handler = Handler(Looper.getMainLooper())

    private lateinit var transition : ButtonTransition
    private lateinit var employeeAPI: EmployeeAPI
    private lateinit var keycloakAPI : KeycloakAPI

    private var isListening : Boolean = false
    private lateinit var  runnable:  Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLeaderRequestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        email = intent.getStringExtra("email")
        password = intent.getStringExtra("password")
        refreshToken = intent.getStringExtra("refreshToken")

        employeeDto = intent.getParcelableExtra("employeeDto")

        val emailErrorObj = ErrorObj(binding.EmailET, binding.EmailETL)

        ClearErrors.clearErrors(listOf(emailErrorObj))

        transition = ButtonTransition(
            binding.leaderRequestLayout,
            binding.LoadingCircle,
            binding.SendRequest,
            this@LeaderRequestActivity)


        if(employeeDto?.leaderRequestEmail !="" && employeeDto?.leaderRequestEmail != null ){
            updateLayout(binding, employeeDto!!.leaderRequestEmail!!)
        }

        employeeAPI = EmployeeAPI()
        keycloakAPI = KeycloakAPI()


        if(employeeDto?.leaderRequestEmail != null && employeeDto?.leaderRequestEmail != "" && employeeDto?.leaderId == null ){
           startRequestLoop(refreshToken!!)
        }


        binding.SendRequest.setOnClickListener(){

            val emailValidation = Validation.ValidateEmail(binding.EmailET.text.toString())

            if(emailValidation.IsValid){
                transition.startLoading()

                if(employeeDto?.leaderRequestEmail !="" && employeeDto?.leaderRequestEmail != null ){

                    performKeycloakAction("delete")
                    stopRequestLoop()

                }
                else{
                    performKeycloakAction("create")
                }
            }
            else{
                binding.EmailETL.error = emailValidation.ErrorMessage
            }

        }

    }


    private fun performKeycloakAction(requestType : String){
        keycloakAPI.getFromRefreshToken(refreshToken!!,
            onSuccess = {data ->
                refreshToken = data.refresh_token

                if(requestType == "create"){
                    createRequest(data.access_token)
                    startRequestLoop(refreshToken!!)
                }
                else if(requestType == "delete"){
                    deleteRequest(data.access_token)

                }

            },
            onFailure = {error->
                if(error == "Refresh token expired"){
                    keycloakAPI.getFromEmailAndPass(email!!,password!!,
                        onSuccess = {data ->

                            refreshToken = data.refresh_token

                            if(requestType == "create"){
                                createRequest(data.access_token)
                                startRequestLoop(refreshToken!!)
                            }
                            else if(requestType == "delete"){
                                deleteRequest(data.access_token)
                            }
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

    private fun createRequest( accessToken : String){
        employeeAPI.createRequest(accessToken, binding.EmailET.text.toString(),
            onSuccess = {requestId ->
                Toast.makeText(this@LeaderRequestActivity, "Request created",Toast.LENGTH_LONG).show()
                updateLayout(binding, binding.EmailET.text.toString())

                employeeDto?.leaderRequestEmail = binding.EmailET.text.toString()
                transition.stopLoading()
            },
            onFailure = {error->
                Toast.makeText(this@LeaderRequestActivity, error,Toast.LENGTH_LONG).show()
                transition.stopLoading()
            })
    }

    private fun deleteRequest(accessToken : String){
        employeeAPI.deleteRequest(accessToken, binding.EmailET.text.toString(),
            onSuccess = {employeeid ->
                Toast.makeText(this@LeaderRequestActivity, "Request deleted",Toast.LENGTH_LONG).show()
                resetLayout(binding)
                employeeDto?.leaderRequestEmail = ""
                transition.stopLoading()
            },
            onFailure = {error->
                Toast.makeText(this@LeaderRequestActivity, error,Toast.LENGTH_LONG).show()

                if(error == "Request not found"){
                    resetLayout(binding)
                    employeeDto?.leaderRequestEmail = ""
                }
                transition.stopLoading()
            })
    }

    private fun startRequestLoop(refreshToken:String) {
        isListening = true

        runnable = object : Runnable {
            override fun run() {
                if (isListening) {
                    refreshTokenRequest(refreshToken, keycloakAPI, employeeAPI)
                    handler.postDelayed(this, 30000)
                }
            }
        }

        handler.postDelayed(runnable, 30000)


    }

    private fun stopRequestLoop() {
        isListening = false
        handler.removeCallbacks(runnable)
    }

    private fun refreshTokenRequest(refreshToken:String, keycloakAPI: KeycloakAPI, employeeAPI: EmployeeAPI) {

        keycloakAPI.getFromRefreshToken(refreshToken,
            onSuccess = {data ->
                employeeAPI.getEmployeeDto(data.access_token,
                    onSuccess = {dto ->
                        employeeDto = dto

                        if(employeeDto!!.leaderId != null){
                            Toast.makeText(this@LeaderRequestActivity,"Leader accepted your request", Toast.LENGTH_LONG).show()
                            val intent = Intent(this@LeaderRequestActivity, LoggedInActivity::class.java)
                            intent.putExtra("email", email)
                            intent.putExtra("password", password)
                            intent.putExtra("leaderId", employeeDto!!.leaderId)
                            startActivity(intent)
                            finish()
                        }
                    },
                    onFailure ={error->
                        Toast.makeText(this@LeaderRequestActivity, error,Toast.LENGTH_LONG).show()
                    })
            },
            onFailure = {error->
                Toast.makeText(this@LeaderRequestActivity, error,Toast.LENGTH_LONG).show()
            })

    }
    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
    private fun updateLayout( binding: ActivityLeaderRequestBinding, leaderEmail :String){
        binding.EmailETL.isEnabled = false
        binding.EmailETL.isHelperTextEnabled = true
        binding.EmailETL.helperText = "Waiting for approve"
        binding.EmailET.setText(leaderEmail)
        binding.SendRequest.text = "Cancel request"
    }

    private fun resetLayout( binding: ActivityLeaderRequestBinding){
        binding.EmailETL.isEnabled = true
        binding.EmailETL.isHelperTextEnabled = false
        binding.EmailET.setText("")
        binding.SendRequest.text = "Send request"
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