package com.dybich.collabdoapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.dybich.collabdoapp.Dtos.EmployeeDto
import com.dybich.collabdoapp.databinding.ActivityLeaderRequestBinding
import com.dybich.collabdoapp.login.ButtonTransition

class LeaderRequestActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLeaderRequestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLeaderRequestBinding.inflate(layoutInflater)
        setContentView(binding.root)



        val email: String? = intent.getStringExtra("email")
        val password : String? = intent.getStringExtra("password")

        val employeeDto : EmployeeDto? = intent.getParcelableExtra("employeeDto")

        Log.d("USERDATA", email.toString())
        Log.d("USERDATA", password.toString())

        val transition = ButtonTransition(
            binding.leaderRequestLayout,
            binding.BtnTV,
            binding.LoadingCircle,
            binding.SendRequest,
            this@LeaderRequestActivity)

        binding.SendRequest.setOnClickListener(){
            transition.startLoading()


        }

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