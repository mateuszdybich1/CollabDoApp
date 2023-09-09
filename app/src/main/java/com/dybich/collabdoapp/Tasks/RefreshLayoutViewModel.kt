package com.dybich.collabdoapp.Tasks

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RefreshLayoutViewModel : ViewModel() {
    var shouldRefresh = MutableLiveData<Boolean>(false)
}