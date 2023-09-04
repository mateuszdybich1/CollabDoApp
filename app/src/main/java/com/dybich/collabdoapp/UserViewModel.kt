package com.dybich.collabdoapp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UserViewModel : ViewModel() {
    val refreshToken = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val isLeader = MutableLiveData<Boolean>()
    val leaderId = MutableLiveData<String>()
}