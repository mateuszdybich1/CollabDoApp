package com.dybich.collabdoapp.login

object EmailValidation {
    fun IsEmailValid(email: String) : Boolean{
        return email.matches(Regex("[A-Za-z0-9._%+-]+@uekat\\.pl$")) &&
                email.contains("@") &&
                !email.contains(" ") && email != ""
    }
}