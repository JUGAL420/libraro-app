package com.techito.libraro.viewmodel

import android.os.CountDownTimer
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AdminAuthViewModel : ViewModel() {

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
    val otpTimerText = MutableLiveData<String>()
    val isResendEnabled = MutableLiveData<Boolean>(false)

    // Forgot Password Data
    val forgotPasswordEmail = MutableLiveData<String>()

    // Status/Loading
    val isLoading = MutableLiveData<Boolean>(false)
    val errorMessage = MutableLiveData<String?>()

    // Navigation LiveData
    val navigateToHome = MutableLiveData<Boolean>()

    private var timer: CountDownTimer? = null

    fun startOtpTimer() {
        timer?.cancel()
        isResendEnabled.value = false
        timer = object : CountDownTimer(120000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                otpTimerText.value = "Resend OTP in <font color='#EC1B27'><b>${seconds}s</b></font>"
            }

            override fun onFinish() {
                otpTimerText.value =
                    "Didn't receive the OTP? <font color='#EC1B27'><b>Resend</b></font>"
                isResendEnabled.value = true
            }
        }.start()
    }

    fun onSignInClicked() {
        // Implement Sign In validation logic here
        // If valid, trigger navigation
        navigateToHome.value = true
    }

    fun onSignUpClicked() {
        // Implement Sign Up logic
    }

    fun onVerifyOtpClicked() {
        // Implement OTP verification logic
    }

    fun onResendOtpClicked() {
        startOtpTimer()
        // Implement Resend OTP logic
    }

    fun onSendInstructionsClicked() {
        // Implement forgot password logic
    }

    fun onNavigationHandled() {
        navigateToHome.value = false
    }

    override fun onCleared() {
        super.onCleared()
        timer?.cancel()
    }
}
