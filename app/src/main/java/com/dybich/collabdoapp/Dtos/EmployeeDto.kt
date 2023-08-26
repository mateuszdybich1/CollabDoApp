package com.dybich.collabdoapp.Dtos

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class EmployeeDto (
    val employeeId : String?,
    val leaderRequestEmail : String?,
    var leaderId : String?
) : Parcelable {

}