package com.dybich.collabdoapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.dybich.collabdoapp.Dtos.EmployeeDto
import com.dybich.collabdoapp.databinding.ActivityLeaderRequestBinding

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

        if(employeeDto!=null){
            Toast.makeText(this@LeaderRequestActivity,employeeDto.employeeId,Toast.LENGTH_LONG).show()
        }

    }
}