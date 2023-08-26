package com.dybich.collabdoapp.Dtos

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class EmployeeDto (
    val employeeId : String?,
    var leaderRequestEmail : String?,
    var leaderId : String?
) : Parcelable {

}