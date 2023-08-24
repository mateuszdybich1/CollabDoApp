package com.dybich.collabdoapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import com.dybich.collabdoapp.databinding.ActivityLeaderRequestBinding
import com.dybich.collabdoapp.databinding.ActivityLoginBinding

class LeaderRequestActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLeaderRequestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLeaderRequestBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}