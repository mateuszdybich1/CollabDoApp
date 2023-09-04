package com.dybich.collabdoapp.FinishedProjects

import androidx.lifecycle.ViewModel
import com.dybich.collabdoapp.Dtos.ProjectDto

class FinishedProjectsViewModel : ViewModel() {
    var isSaved = false
    var pageNumber = 1
    var miliseconds : Long?=null
    var projectList: ArrayList<ProjectDto>? = null
}