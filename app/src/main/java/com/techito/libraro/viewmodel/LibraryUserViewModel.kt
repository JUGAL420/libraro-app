package com.techito.libraro.viewmodel

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techito.libraro.LibraroApp
import com.techito.libraro.R
import com.techito.libraro.data.remote.RetrofitClient
import com.techito.libraro.repository.MainRepository
import com.techito.libraro.utils.NetworkResult
import kotlinx.coroutines.launch
import java.io.File

class LibraryUserViewModel : ViewModel() {

    private val repository = MainRepository(RetrofitClient.apiService)

    // Form Data
    val fullName = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    val mobileNumber = MutableLiveData<String>()
    val role = MutableLiveData<String>()
    val branches = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val confirmPassword = MutableLiveData<String>()

    val selectedImageUri = MutableLiveData<Uri>()
    var selectedFile: File? = null

    // UI State
    val isLoading = MutableLiveData<Boolean>(false)
    val errorMessage = MutableLiveData<String?>()
    val userActionSuccess = MutableLiveData<String?>()

    private fun getString(resId: Int): String {
        return LibraroApp.instance.getString(resId)
    }

    fun setImage(uri: Uri, file: File) {
        selectedImageUri.value = uri
        selectedFile = file
    }

    fun onAddUserClicked() {
        if (validateForm()) {
            performAddUser()
        }
    }

    fun onUpdateUserClicked() {
        if (validateForm(isEdit = true)) {
            performUpdateUser()
        }
    }

    private fun validateForm(isEdit: Boolean = false): Boolean {
        if (fullName.value.isNullOrBlank()) {
            errorMessage.value = "Please enter full name"
            return false
        }
        if (email.value.isNullOrBlank()) {
            errorMessage.value = "Please enter email id"
            return false
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.value ?: "").matches()) {
            errorMessage.value = "Please enter a valid email address"
            return false
        }
        if (mobileNumber.value.isNullOrBlank()) {
            errorMessage.value = "Please enter mobile number"
            return false
        }
        if ((mobileNumber.value?.length ?: 0) != 10) {
            errorMessage.value = "Please enter a valid 10-digit mobile number"
            return false
        }
        if (role.value.isNullOrBlank()) {
            errorMessage.value = "Please select user role"
            return false
        }
        if (branches.value.isNullOrBlank()) {
            errorMessage.value = "Please choose branches"
            return false
        }

        // Passwords are only mandatory for new users
        if (!isEdit) {
            if (password.value.isNullOrBlank()) {
                errorMessage.value = "Please enter password"
                return false
            }
            if ((password.value?.length ?: 0) < 6) {
                errorMessage.value = "Password must be at least 6 characters long"
                return false
            }
            if (password.value != confirmPassword.value) {
                errorMessage.value = "Passwords do not match"
                return false
            }
        } else if (!password.value.isNullOrBlank()) {
            // If user enters password during edit, validate it
            if ((password.value?.length ?: 0) < 6) {
                errorMessage.value = "Password must be at least 6 characters long"
                return false
            }
            if (password.value != confirmPassword.value) {
                errorMessage.value = "Passwords do not match"
                return false
            }
        }

        if(!isEdit){
            if (selectedFile == null) {
                errorMessage.value = "Please select a profile picture"
                return false
            }
        }

        return true
    }

    private fun performAddUser() {
        isLoading.value = true
        viewModelScope.launch {
            // Logic to send multipart request to server
            // result = repository.addLibraryUser(...)
            
            // For now, simulating success
            isLoading.value = false
            userActionSuccess.value = "User added successfully"
        }
    }

    private fun performUpdateUser() {
        isLoading.value = true
        viewModelScope.launch {
            // Logic to send multipart request to server
            // result = repository.updateLibraryUser(...)
            
            // For now, simulating success
            isLoading.value = false
            userActionSuccess.value = "User updated successfully"
        }
    }

    fun onErrorHandled() {
        errorMessage.value = null
    }

    fun onActionSuccessHandled() {
        userActionSuccess.value = null
    }
}
