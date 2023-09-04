package com.dybich.collabdoapp.Projects

import androidx.lifecycle.ViewModel
import com.dybich.collabdoapp.Dtos.ProjectDto

class ProjectsViewModel : ViewModel() {
    var isSaved = false
    var projectList: ArrayList<ProjectDto>? = null
}