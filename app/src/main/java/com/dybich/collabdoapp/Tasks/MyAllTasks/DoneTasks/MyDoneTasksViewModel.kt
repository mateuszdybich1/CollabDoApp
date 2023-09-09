package com.dybich.collabdoapp.Tasks.MyAllTasks.DoneTasks

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dybich.collabdoapp.Dtos.TaskDto

class MyDoneTasksViewModel : ViewModel() {
    var pageNumber = MutableLiveData<Int>()
    var miliseconds = MutableLiveData<Long>()
    var myTasks= MutableLiveData<ArrayList<TaskDto>>()
}