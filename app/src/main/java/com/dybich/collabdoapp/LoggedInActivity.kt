package com.dybich.collabdoapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dybich.collabdoapp.databinding.ActivityLoggedInBinding

class LoggedInActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLoggedInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoggedInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val email: String? = intent.getStringExtra("email")

        val password : String? = intent.getStringExtra("password")

        val leaderId : String? = intent.getStringExtra("leaderId")
    }
}