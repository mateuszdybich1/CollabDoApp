package com.dybich.collabdoapp.Tasks.WaitingTasks

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dybich.collabdoapp.Dtos.TaskDto

class WaitingTasksViewModel : ViewModel() {
    var isSaved = MutableLiveData<Boolean>()
    var isLoading = MutableLiveData<Boolean>()
    var pageNumber = MutableLiveData<Int>()
    var miliseconds = MutableLiveData<Long>()
    var myTasks= MutableLiveData<ArrayList<TaskDto>>()
}