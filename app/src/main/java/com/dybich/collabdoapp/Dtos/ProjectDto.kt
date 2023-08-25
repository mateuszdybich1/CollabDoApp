package com.dybich.collabdoapp.Dtos

import com.dybich.collabdoapp.Priority

data class ProjectDto(val projectId : String,
    val name : String,
    val priority : Priority)
