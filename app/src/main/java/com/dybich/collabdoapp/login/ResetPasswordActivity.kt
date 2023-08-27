package com.dybich.collabdoapp.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.dybich.collabdoapp.API.UserAPI
import com.dybich.collabdoapp.ButtonTransition
import com.dybich.collabdoapp.databinding.ActivityResetPasswordBinding

class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var binding : ActivityResetPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val emailErrorObj = ErrorObj(binding.EmailET, binding.EmailETL)

        ClearErrors.clearErrors(listOf(emailErrorObj))

        val transition = ButtonTransition(binding.ResetPasswordLayout, binding.LoadingCircle,binding.ResetPassword, this@ResetPasswordActivity)


        val userAPI = UserAPI()


        binding.ResetPassword.setOnClickListener(){

            val emailValidation = Validation.ValidateEmail(binding.EmailET.text.toString())

            if(emailValidation.IsValid){
                transition.startLoading()
                userAPI.resetPassword(binding.EmailET.text.toString(),
                    onSuccess = {message ->
                        Toast.makeText(this@ResetPasswordActivity,message,Toast.LENGTH_LONG).show()
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                    },
                    onFailure = {error->
                        Toast.makeText(this@ResetPasswordActivity,error,Toast.LENGTH_LONG).show()
                        transition.stopLoading()
                    })
            }
            else{
                binding.EmailETL.error = emailValidation.ErrorMessage
            }
        }
    }
}