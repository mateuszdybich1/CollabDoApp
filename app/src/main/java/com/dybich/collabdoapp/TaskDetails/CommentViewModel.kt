package com.dybich.collabdoapp.TaskDetails

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dybich.collabdoapp.Dtos.TaskDto
import com.dybich.collabdoapp.Priority
import com.dybich.collabdoapp.TaskStatus

class CommentViewModel : ViewModel() {
    var taskId = MutableLiveData<String>()
    var name = MutableLiveData<String>()
    var description =MutableLiveData<String>()
    var priority = MutableLiveData<Priority>()
    var assignedUser = MutableLiveData<String>()
    var taskStatus = MutableLiveData<TaskStatus>()
    var deadline = MutableLiveData<Long>()
}