package com.dybich.collabdoapp.Dtos


import com.dybich.collabdoapp.Priority
import com.dybich.collabdoapp.TaskStatus

data class TaskDto(val taskId:String?, val projectId:String, val name:String, val priority: Priority, val userEmail:String, val status: TaskStatus, val assignedId:String?, val deadline : Long)
