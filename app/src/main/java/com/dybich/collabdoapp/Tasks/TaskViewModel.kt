package com.dybich.collabdoapp.Tasks

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TaskViewModel : ViewModel() {
    var projectId = MutableLiveData<String>()
}