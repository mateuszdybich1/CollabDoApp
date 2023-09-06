package com.dybich.collabdoapp.Projects

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dybich.collabdoapp.Dtos.ProjectDto

class ProjectsViewModel : ViewModel() {
    var isSaved = MutableLiveData<Boolean>()
    var pageNumber = MutableLiveData<Int>()
    var miliseconds = MutableLiveData<Long>()
    var projectList=MutableLiveData<ArrayList<ProjectDto>>()
}