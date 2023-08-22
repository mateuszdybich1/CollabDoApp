package com.dybich.collabdoapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.dybich.collabdoapp.login.LoginActivity

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var img: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen_layout)

        img = findViewById(R.id.Splash_Screen_IV)
        img.animate().setDuration(2000).alpha(1f).withEndAction {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}