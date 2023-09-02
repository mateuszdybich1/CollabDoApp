package com.dybich.collabdoapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.dybich.collabdoapp.API.EmployeeAPI
import com.dybich.collabdoapp.API.KeycloakAPI
import com.dybich.collabdoapp.API.UserAPI
import com.dybich.collabdoapp.login.LoginActivity
import com.dybich.collabdoapp.databinding.ActivityLoggedInBinding

class LoggedInActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLoggedInBinding

    private lateinit var keycloakAPI : KeycloakAPI

    private lateinit var userAPI : UserAPI

    private lateinit var employeeAPI : EmployeeAPI

    private lateinit var snackbar : Snackbar

    private var email : String? = null
    private var password : String? = null
    private var leaderId : String? = null
    private var isUserLeader : Boolean = false
    private var refreshToken : String? = null

    private var isListening : Boolean = false
    private lateinit var  runnable:  Runnable
    private val handler = Handler(Looper.getMainLooper())

    private val sharedViewModel: SharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoggedInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        email= intent.getStringExtra("email")
        password = intent.getStringExtra("password")
        leaderId = intent.getStringExtra("leaderId")
        isUserLeader = intent.getBooleanExtra("isLeader",false)
        refreshToken = intent.getStringExtra("refreshToken")

        userAPI = UserAPI()
        keycloakAPI = KeycloakAPI()
        employeeAPI = EmployeeAPI()

        snackbar = com.dybich.collabdoapp.Snackbar(binding.root,this@LoggedInActivity)

        val employeeRequestsMenuItem = binding.navigationView.menu.findItem(R.id.employeeRequestsFragment)
        employeeRequestsMenuItem.isVisible = isUserLeader

        performKeycloakAction()


        sharedViewModel.refreshToken.value = refreshToken
        sharedViewModel.email.value = email
        sharedViewModel.password.value = password
        sharedViewModel.isLeader.value = isUserLeader
        sharedViewModel.leaderId.value = leaderId


        val navHostFragment = supportFragmentManager.findFragmentById(R.id.drawerFragmentCV) as NavHostFragment
        val navController = navHostFragment.navController
        binding.navigationView.setupWithNavController(navController)



        binding.topAppBar.setNavigationOnClickListener {
            binding.drawerLayout.open()
        }


        binding.navigationView.setNavigationItemSelectedListener{ menuItem ->

            menuItem.isChecked

            binding.drawerLayout.close()
            menuItem.onNavDestinationSelected(navHostFragment.navController)

            when(menuItem.itemId){
                R.id.projectsFragment->{
                    binding.topAppBar.title = "Projects"
                }
                R.id.projectGroupFragment->{
                    binding.topAppBar.title = "Project Group"
                }
                R.id.employeeRequestsFragment->{
                    binding.topAppBar.title = "Employee Requests"
                }
                R.id.logout->{
                    val intent = Intent(this@LoggedInActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
            true
        }
        if(!isUserLeader){
            startRequestLoop(refreshToken!!)
        }

    }

    private fun startRequestLoop(refreshToken:String) {
        isListening = true

        runnable = object : Runnable {
            override fun run() {
                if (isListening) {
                    refreshTokenRequest(refreshToken)
                    handler.postDelayed(this, 30000)
                }
            }
        }
        handler.postDelayed(runnable, 30000)
    }
    private fun refreshTokenRequest(refreshToken:String) {

        keycloakAPI.getFromRefreshToken(refreshToken,
            onSuccess = {data ->
                employeeAPI.getEmployeeDto(data.access_token,
                    onSuccess = {employeeDto ->
                        if((employeeDto.leaderRequestEmail == null || employeeDto.leaderRequestEmail =="") && employeeDto.leaderId==null){
                            Toast.makeText(this,"Leader removed you from group", Toast.LENGTH_LONG).show()
                            val intent = Intent(this@LoggedInActivity, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                            isListening = false
                        }
                    },
                    onFailure = {error->
                        snackbar.show(error)
                    })
            },
            onFailure = {error->
                snackbar.show(error)
            })

    }


    private fun getUserDto( accessToken : String){
        userAPI.getUserDto(accessToken,
            onSuccess = {userDto ->
                val headerView: View = binding.navigationView.getHeaderView(0)

                val userUsernameTextView: TextView = headerView.findViewById(R.id.userUsername)
                val userEmailTextView: TextView = headerView.findViewById(R.id.userEmail)

                userUsernameTextView.text = userDto.username
                userEmailTextView.text = userDto.email

            },
            onFailure = {error->
                snackbar.show(error)
            })
    }
    private fun performKeycloakAction(){
        keycloakAPI.getFromRefreshToken(refreshToken!!,
            onSuccess = {data ->
                refreshToken = data.refresh_token
                getUserDto(data.access_token)
            },
            onFailure = {error->
                if(error == "Refresh token expired"){
                    keycloakAPI.getFromEmailAndPass(email!!,password!!,
                        onSuccess = {data ->
                            refreshToken = data.refresh_token
                            getUserDto(data.access_token)
                        },
                        onFailure = {err->
                            snackbar.show(err)
                        })
                }
                else{
                    snackbar.show(error)
                }

            })
    }

    private var doubleBackToExitPressedOnce = false
    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finishAffinity()
            finish()
        }

        this.doubleBackToExitPressedOnce = true
        snackbar.show("click BACK again to exit")

        Handler(Looper.getMainLooper()).postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)
    }


}