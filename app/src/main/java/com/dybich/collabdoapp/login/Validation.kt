package com.dybich.collabdoapp.login

object Validation {
    fun ValidateEmail(email: String) : ValidationResponse{
        if(email == ""){
            return  ValidationResponse(false,"Empty email")
        }
        else if(email.contains(" ")){
            return  ValidationResponse(false,"Email mustn't contain spaces")
        }
        else if(!email.contains("@")){
            return  ValidationResponse(false,"Email must contain @")
        }
        else if(!email.matches(Regex("[A-Za-z0-9._%+-]+@uekat\\.pl$"))){
            return  ValidationResponse(false,"Email mus end with uekat.pl")
        }
        else{
            return  ValidationResponse(true,"")
        }
    }
    fun ValidateUsername(username : String) : ValidationResponse{
        if(username == "" ){
            return ValidationResponse(false,"Nickname is empty")
        }
        else if(username.contains(" ")){
            return ValidationResponse(false,"Nickname mustn't contain spaces")
        }
        else if(username.length > 15){
            return ValidationResponse(false,"Nickname too long")
        }
        else {
            return ValidationResponse(true,"")
        }
    }

    fun ValidatePassword(password : String) :ValidationResponse{
        if(password == "" ){
            return  ValidationResponse(false,"Empty password")
        }
        else if(password.contains(" ")){
            return ValidationResponse(false,"Password mustn't contain spaces")
        }
        else if( password.length < 6){
            return ValidationResponse(false,"Password too short")
        }
        else if(password.length > 20){
            return ValidationResponse(false, "Password too long")
        }
        else {
            return ValidationResponse(true,"")

        }
    }

    fun ValidateRepeatPassword(password: String, repeatPass : String) : ValidationResponse{

        if(repeatPass != password && password != "" ){
            return ValidationResponse(false,"Repeat password is incorrect")
        }
        else{
            return ValidationResponse(true,"")
        }

    }
}