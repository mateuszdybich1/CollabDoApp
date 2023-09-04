package com.dybich.collabdoapp.Dtos

import android.os.Parcelable
import com.dybich.collabdoapp.Priority
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProjectDto(val projectId : String?,
    val name : String,
    val priority : Priority) : Parcelable {

}
