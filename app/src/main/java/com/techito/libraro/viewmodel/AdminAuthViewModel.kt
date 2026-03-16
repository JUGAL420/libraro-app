package com.techito.libraro.viewmodel

import android.os.CountDownTimer
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techito.libraro.LibraroApp
import com.techito.libraro.R
import com.techito.libraro.data.remote.RetrofitClient
import com.techito.libraro.model.LoginResponse
import com.techito.libraro.model.OtpVerificationResponse
import com.techito.libraro.model.RegisterData
import com.techito.libraro.repository.MainRepository
import com.techito.libraro.utils.NetworkResult
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.regex.Pattern

class AdminAuthViewModel : ViewModel() {

    private val mainRepository = MainRepository(RetrofitClient.apiService)
    private val preferenceManager = LibraroApp.preferenceManager

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
    val libraryId = MutableLiveData<String>()
    val userEmail = MutableLiveData<String>()
    val verificationCode = MutableLiveData<String>()
    val otpTimerText = MutableLiveData<String>()
    val isResendEnabled = MutableLiveData<Boolean>(false)
    val isVerifyEnabled = MutableLiveData<Boolean>(true)

    // Forgot Password Data
    val forgotPasswordEmail = MutableLiveData<String>()
    val navigateBack = MutableLiveData<String?>()

    // Status/Loading
    val isLoading = MutableLiveData<Boolean>(false)
    val errorMessage = MutableLiveData<String?>()

    // Navigation LiveData
    val navigateToHome = MutableLiveData<Boolean>()
    val navigateToOtp = MutableLiveData<String?>() // Passes the libraryId
    val navigateToPlanSelection = MutableLiveData<OtpVerificationResponse?>()

    private var timer: CountDownTimer? = null

    private fun getString(resId: Int): String {
        return LibraroApp.instance.getString(resId)
    }

    init {
        loadRememberMeDetails()
    }

    private fun loadRememberMeDetails() {
        viewModelScope.launch {
            val isChecked = preferenceManager.isRememberMeChecked.first()
            if (isChecked) {
                signInEmail.value = preferenceManager.rememberEmail.first() ?: ""
                signInPassword.value = preferenceManager.rememberPassword.first() ?: ""
                rememberMe.value = true
            }
        }
    }

    fun startOtpTimer() {
        timer?.cancel()
        isResendEnabled.value = false
        isVerifyEnabled.value = true
        timer = object : CountDownTimer(120000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                otpTimerText.value = "Resend OTP in <font color='#EC1B27'><b>${seconds}s</b></font>"
            }

            override fun onFinish() {
                otpTimerText.value =
                    "Didn't receive the OTP? <font color='#EC1B27'><b>Resend</b></font>"
                isResendEnabled.value = true
                isVerifyEnabled.value = false
            }
        }.start()
    }

    fun onSignInClicked() {
        val email = signInEmail.value
        val password = signInPassword.value

        if (email.isNullOrBlank()) {
            errorMessage.value = getString(R.string.err_enter_email)
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            errorMessage.value = getString(R.string.err_invalid_email)
            return
        }
        if (password.isNullOrBlank()) {
            errorMessage.value = getString(R.string.err_enter_password)
            return
        }
        if (password.length < 8) {
            errorMessage.value = getString(R.string.err_8_characters_password)
            return
        }
        if (!isPasswordStrong(password)) {
            errorMessage.value = getString(R.string.err_strong_password)
            return
        }

        performSignIn(email, password)
    }

    private fun performSignIn(email: String, password: String) {
        isLoading.value = true
        viewModelScope.launch {
            val result = mainRepository.adminLogin(email, password)
            isLoading.value = false
            when (result) {
                is NetworkResult.Success -> {
                    if (result.data?.status == true) {
                        handleLoginSuccess(result.data)
                    } else {
                        errorMessage.value = result.data?.message ?: "Login failed"
                    }
                }

                is NetworkResult.Error -> {
                    errorMessage.value = result.message
                }

                is NetworkResult.Unauthorized -> {
                    errorMessage.value = result.message
                }

                else -> {
                    errorMessage.value = getString(R.string.error_something_went_wrong)
                }
            }

        }
    }

    private suspend fun handleLoginSuccess(response: LoginResponse) {
        // Save Auth Token and basic details
        response.token?.let {
            preferenceManager.saveAuthToken(it)
        }
        response.data?.libraryId?.let{
            preferenceManager.saveLibraryId(it.toString())
        }
        response.userType?.let{
            preferenceManager.saveUserType(it)
        }

        // Handle Remember Me
        preferenceManager.saveRememberMeDetails(
            signInEmail.value,
            signInPassword.value,
            rememberMe.value ?: false
        )

        if (response.isEmailVerified == 1 && (response.isLastStep ?: 0) > 3) {
            preferenceManager.setLoggedIn(true)
            navigateToHome.value = true
        } else if (response.isEmailVerified != 1) {
            preferenceManager.libraryId.first()?.let{
                onLoginSendOtp(it)
            }

        } else if (response.isLastStep == 0) {
            navigateToPlanSelection.value = OtpVerificationResponse(
                RegisterData(response.libraryId),
                response.isEmailVerified,
                response.message,
                response.status,
                response.token,
                response.userType
            )
        }
    }

    fun onLoginSendOtp(libraryId: String) {
        isLoading.value = true
        viewModelScope.launch {
            val result = mainRepository.resendOtp(libraryId)
            isLoading.value = false
            when (result) {
                is NetworkResult.Success -> {
                    if (result.data?.status == true) {
                        navigateToOtp.value = libraryId
                    } else {
                        errorMessage.value = result.data?.message ?: "Failed to resend OTP"
                    }
                }

                is NetworkResult.Error -> {
                    errorMessage.value = result.message
                }

                is NetworkResult.Unauthorized -> {
                    errorMessage.value = result.message
                }

                else -> {
                    errorMessage.value = getString(R.string.error_something_went_wrong)
                }
            }
        }
    }

    /**
     * Modern strong password validation:
     * - Minimum 8 characters
     * - At least one uppercase letter
     * - At least one lowercase letter
     * - At least one digit
     * - At least one special character (@$!%*?&)
     */
    private fun isPasswordStrong(password: String): Boolean {
        val passwordPattern = Pattern.compile(
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$"
        )
        return passwordPattern.matcher(password).matches()
    }

    fun onSignUpClicked() {
        val name = libraryName.value
        val email = signUpEmail.value

        val mobile = mobileNumber.value?.replace(" ", "")
            ?.removePrefix("+91")
            ?.removePrefix("91") ?: ""

        val password = signUpPassword.value

        if (name.isNullOrBlank()) {
            errorMessage.value = getString(R.string.err_enter_library_name)
            return
        }
        if (email.isNullOrBlank()) {
            errorMessage.value = getString(R.string.err_enter_email)
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            errorMessage.value = getString(R.string.err_invalid_email)
            return
        }
        if (mobile.isEmpty()) {
            errorMessage.value = getString(R.string.err_enter_mobile)
            return
        }
        if (mobile.length != 10) {
            errorMessage.value = getString(R.string.err_invalid_mobile)
            return
        }
        if (password.isNullOrBlank()) {
            errorMessage.value = getString(R.string.err_enter_password)
            return
        }
        if (password.length < 8) {
            errorMessage.value = getString(R.string.err_8_characters_password)
            return
        }
        if (!isPasswordStrong(password)) {
            errorMessage.value = getString(R.string.err_strong_password)
            return
        }

        performSignUp(name, email, mobile, password)
    }

    private fun performSignUp(name: String, email: String, mobile: String, password: String) {
        isLoading.value = true
        viewModelScope.launch {
            val result = mainRepository.adminRegister(name, email, mobile, password)
            isLoading.value = false
            when (result) {
                is NetworkResult.Success -> {
                    if (result.data?.status == true) {
                        result.data.data?.libraryId?.let {
                            navigateToOtp.value = it.toString()
                        }
                    } else {
                        errorMessage.value =
                            result.data?.message ?: getString(R.string.error_something_went_wrong)
                    }
                }

                is NetworkResult.Error -> {
                    errorMessage.value = result.message
                }

                is NetworkResult.Unauthorized -> {
                    errorMessage.value = result.message
                }

                else -> {
                    errorMessage.value = getString(R.string.error_something_went_wrong)
                }
            }

        }
    }

    fun onVerifyOtpClicked() {
        val id = libraryId.value ?: ""
        val code = verificationCode.value ?: ""
        if (isVerifyEnabled.value != true) {
            return
        }
        if (code.length != 6) {
            errorMessage.value = "Please enter a valid 6-digit verification code"
            return
        }

        isLoading.value = true
        viewModelScope.launch {
            val otpVerificationResult = mainRepository.verifyOtp(id, code)
            isLoading.value = false
            when (otpVerificationResult) {
                is NetworkResult.Success -> {
                    if (otpVerificationResult.data?.status == true) {
                        // Save Auth Token and basic details
                        otpVerificationResult.data.token?.let {
                            preferenceManager.saveAuthToken(it)
                        }
                        otpVerificationResult.data.data?.libraryId?.let{
                            preferenceManager.saveLibraryId(it.toString())
                        }
                        otpVerificationResult.data.userType?.let{
                            preferenceManager.saveUserType(it)
                        }
                        navigateToPlanSelection.value = otpVerificationResult.data
                    } else {
                        errorMessage.value =
                            otpVerificationResult.data?.message ?: "OTP verification failed"
                    }
                }

                is NetworkResult.Error -> {
                    errorMessage.value = otpVerificationResult.message
                }

                is NetworkResult.Unauthorized -> {
                    errorMessage.value = otpVerificationResult.message
                }

                else -> {
                    errorMessage.value = getString(R.string.error_something_went_wrong)
                }
            }

        }
    }

    fun onResendOtpClicked() {
        if (isResendEnabled.value == true) {
            isLoading.value = true
            viewModelScope.launch {
                val id = libraryId.value ?: ""
                val result = mainRepository.resendOtp(id)
                isLoading.value = false
                when (result) {
                    is NetworkResult.Success -> {
                        if (result.data?.status == true) {
                            startOtpTimer() // This re-enables the Verify button
                            errorMessage.value = result.data.message ?: "OTP Resent Successfully"
                        } else {
                            errorMessage.value = result.data?.message ?: "Failed to resend OTP"
                        }
                    }

                    is NetworkResult.Error -> {
                        errorMessage.value = result.message
                    }

                    is NetworkResult.Unauthorized -> {
                        errorMessage.value = result.message
                    }

                    else -> {
                        errorMessage.value = getString(R.string.error_something_went_wrong)
                    }
                }

            }
        }
    }

    fun onSendInstructionsClicked() {
        val email = forgotPasswordEmail.value
        if (email.isNullOrBlank()) {
            errorMessage.value = getString(R.string.err_enter_email)
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            errorMessage.value = getString(R.string.err_invalid_email)
            return
        }

        isLoading.value = true
        viewModelScope.launch {
            val result = mainRepository.forgotPassword(email)
            isLoading.value = false
            when (result) {
                is NetworkResult.Success -> {
                    if (result.data?.status == true) {
                        navigateBack.value = result.data.message ?: "Instructions sent successfully"
                    } else {
                        errorMessage.value = result.data?.message ?: getString(R.string.error_something_went_wrong)
                    }
                }
                is NetworkResult.Error -> {
                    errorMessage.value = result.message
                }
                is NetworkResult.Unauthorized -> {
                    errorMessage.value = result.message
                }
                else -> {
                    errorMessage.value = getString(R.string.error_something_went_wrong)
                }
            }

        }
    }

    fun onNavigationHandled() {
        navigateToHome.value = false
        navigateToOtp.value = null
        navigateToPlanSelection.value = null
        navigateBack.value = null
    }

    fun onErrorHandled() {
        errorMessage.value = null
    }

    override fun onCleared() {
        super.onCleared()
        timer?.cancel()
    }
}
