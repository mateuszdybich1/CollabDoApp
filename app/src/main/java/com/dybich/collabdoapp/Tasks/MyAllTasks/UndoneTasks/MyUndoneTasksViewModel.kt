package com.dybich.collabdoapp.Tasks.MyAllTasks.UndoneTasks

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dybich.collabdoapp.Dtos.TaskDto

class MyUndoneTasksViewModel : ViewModel() {
    var pageNumber = MutableLiveData<Int>()
    var miliseconds = MutableLiveData<Long>()
    var myTasks= MutableLiveData<ArrayList<TaskDto>>()
}