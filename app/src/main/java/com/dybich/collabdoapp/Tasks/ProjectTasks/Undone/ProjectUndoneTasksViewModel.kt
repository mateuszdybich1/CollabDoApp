package com.dybich.collabdoapp.Tasks.ProjectTasks.Undone

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dybich.collabdoapp.Dtos.TaskDto

class ProjectUndoneTasksViewModel : ViewModel() {
    var pageNumber = MutableLiveData<Int>()
    var miliseconds = MutableLiveData<Long>()
    var projectsTasks= MutableLiveData<ArrayList<TaskDto>>()
}