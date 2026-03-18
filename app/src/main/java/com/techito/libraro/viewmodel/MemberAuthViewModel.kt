package com.techito.libraro.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MemberAuthViewModel : ViewModel() {

    // Sign In Data
    val signInEmailDobUid = MutableLiveData<String>()
    val signInPassword = MutableLiveData<String>()
    val rememberMe = MutableLiveData<Boolean>(false)



    // Status/Loading
    val isLoading = MutableLiveData<Boolean>(false)
    val errorMessage = MutableLiveData<String?>()

    fun onSignInClicked() {
        // Implement Sign In logic
    }

}
