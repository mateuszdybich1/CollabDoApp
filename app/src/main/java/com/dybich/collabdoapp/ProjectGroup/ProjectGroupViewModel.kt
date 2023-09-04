package com.dybich.collabdoapp.ProjectGroup

import androidx.lifecycle.ViewModel
import com.dybich.collabdoapp.Dtos.EmployeeDto
import com.dybich.collabdoapp.Dtos.ProjectDto

class ProjectGroupViewModel : ViewModel() {
    var isSaved = false
    var groupList: ArrayList<EmployeeDto>? = null
}