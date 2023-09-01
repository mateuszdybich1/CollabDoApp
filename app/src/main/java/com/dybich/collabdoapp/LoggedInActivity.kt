package com.dybich.collabdoapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.dybich.collabdoapp.API.KeycloakAPI
import com.dybich.collabdoapp.API.UserAPI
import com.dybich.collabdoapp.databinding.ActivityLoggedInBinding
import com.dybich.collabdoapp.login.LoginActivity

class LoggedInActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLoggedInBinding

    private lateinit var keycloakAPI : KeycloakAPI

    private lateinit var userAPI : UserAPI

    private lateinit var snackbar : Snackbar

    private var email : String? = null
    private var password : String? = null
    private var leaderId : String? = null
    private var isUserLeader : Boolean = false
    private var refreshToken : String? = null

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

        snackbar = com.dybich.collabdoapp.Snackbar(binding.root,this@LoggedInActivity)

        val employeeRequestsMenuItem = binding.navigationView.menu.findItem(R.id.employeeRequestsFragment)
        employeeRequestsMenuItem.isVisible = isUserLeader

        performKeycloakAction()


        sharedViewModel.refreshToken.value = refreshToken
        sharedViewModel.email.value = email
        sharedViewModel.password.value = password
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


}