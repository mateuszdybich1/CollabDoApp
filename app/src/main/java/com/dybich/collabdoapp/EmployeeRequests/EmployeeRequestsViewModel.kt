package com.dybich.collabdoapp.EmployeeRequests

import androidx.lifecycle.ViewModel
import com.dybich.collabdoapp.Dtos.EmployeeRequestDto

class EmployeeRequestsViewModel: ViewModel() {
    var isSaved = false
    var requestsList : ArrayList<EmployeeRequestDto>? = null
}