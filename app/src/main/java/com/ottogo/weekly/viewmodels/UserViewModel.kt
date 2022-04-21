package com.ottogo.weekly.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UserViewModel: ViewModel() {

    var token by mutableStateOf<String?>("8375e2ec5ea97021bcf0ecb5bad9304cce0b6ef7")

}