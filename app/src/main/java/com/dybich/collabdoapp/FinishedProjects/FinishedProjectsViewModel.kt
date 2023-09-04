package com.dybich.collabdoapp.FinishedProjects

import androidx.lifecycle.ViewModel
import com.dybich.collabdoapp.Dtos.ProjectDto

class FinishedProjectsViewModel : ViewModel() {
    var isSaved = false
    var projectList: ArrayList<ProjectDto>? = null
}