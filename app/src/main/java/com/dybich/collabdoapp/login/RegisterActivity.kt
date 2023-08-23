package com.dybich.collabdoapp.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.dybich.collabdoapp.Dtos.UserRegisterDto
import com.dybich.collabdoapp.R
import com.dybich.collabdoapp.RetrofitAPI
import com.dybich.collabdoapp.databinding.ActivityLoginBinding
import com.dybich.collabdoapp.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding : ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val transition = ButtonTransition(
            binding.registerLayout,
            binding.RegisterBTN,
            binding.RegisterPB,
            binding.RegisterBTN,
            this@RegisterActivity)

        val usernameErrorObj = ErrorObj(binding.NicknameET, binding.NicknameETL)
        val emailErrorObj = ErrorObj(binding.RegEmailET, binding.RegEmailETL)
        val passErrorObj = ErrorObj(binding.RegPasswordET, binding.RegPasswordETL)
        val repPassErrorObj = ErrorObj(binding.RepPassET, binding.RepPassETL)

        ClearErrors.clearErrors(listOf(usernameErrorObj,emailErrorObj,passErrorObj,repPassErrorObj))

        binding.RegisterBTN.setOnClickListener(){


            val username = binding.NicknameET.text.toString()
            val email = binding.RegEmailET.text.toString()
            val password = binding.RegPasswordET.text.toString()
            val repeatPass = binding.RepPassET.text.toString()
            val isLeader = binding.registerS.isChecked

            val usernameValidation = Validation.ValidateUsername(username)
            val emailValidation = Validation.ValidateEmail(email)
            val passwordValidation = Validation.ValidatePassword(password)
            val repeatPassValidation = Validation.ValidateRepeatPassword(password,repeatPass)

            if(usernameValidation.IsValid &&
                emailValidation.IsValid &&
                passwordValidation.IsValid &&
                repeatPassValidation.IsValid){

                transition.startLoading()

                val registerObj = UserRegisterDto(username,email,password,isLeader)

                RetrofitAPI.registerUser(registerObj,
                    onSuccess = { userId ->
                        Log.d("TAG",userId)
                        Toast.makeText(this,userId,Toast.LENGTH_LONG).show()
                        transition.stopLoading()
                    },
                    onFailure = { error ->
                        Toast.makeText(this,error.message.toString(),Toast.LENGTH_LONG).show()
                        transition.stopLoading()
                    })



            }
            else{
                if(!usernameValidation.IsValid){
                    binding.NicknameETL.error = usernameValidation.ErrorMessage
                }
                if(!emailValidation.IsValid){
                    binding.RegEmailETL.error = emailValidation.ErrorMessage
                }
                if(!passwordValidation.IsValid){
                    binding.RegPasswordETL.error = passwordValidation.ErrorMessage
                }
                if(!repeatPassValidation.IsValid){
                    binding.RepPassETL.error = repeatPassValidation.ErrorMessage
                }
            }
        }


    }
}