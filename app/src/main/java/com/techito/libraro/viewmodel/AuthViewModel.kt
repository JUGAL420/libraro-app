package com.techito.libraro.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AuthViewModel : ViewModel() {

    // Sign In Data
    val signInEmail = MutableLiveData<String>()
    val signInPassword = MutableLiveData<String>()
    val rememberMe = MutableLiveData<Boolean>(false)

    // Sign Up Data
    val libraryName = MutableLiveData<String>()
    val signUpEmail = MutableLiveData<String>()
    val mobileNumber = MutableLiveData<String>()
    val signUpPassword = MutableLiveData<String>()

    // OTP Data
    val verificationCode = MutableLiveData<String>()

    // Status/Loading
    val isLoading = MutableLiveData<Boolean>(false)
    val errorMessage = MutableLiveData<String?>()

    fun onSignInClicked() {
        // Implement Sign In logic
    }

    fun onSignUpClicked() {
        // Implement Sign Up logic
    }

    fun onVerifyOtpClicked() {
        // Implement OTP verification logic
    }
}
