package com.dybich.collabdoapp.Dtos

data class UserRegisterDto(
    val username : String,
    val email : String,
    val password : String,
    val isLeader : Boolean
) {

}