package com.techito.libraro.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techito.libraro.LibraroApp
import com.techito.libraro.data.remote.RetrofitClient
import com.techito.libraro.model.LibraryDetail
import com.techito.libraro.repository.AuthRepository
import com.techito.libraro.repository.MainRepository
import com.techito.libraro.utils.NetworkResult
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val repository = MainRepository(RetrofitClient.apiService)
    private val authRepository = AuthRepository(RetrofitClient.apiService)
    private val preferenceManager = LibraroApp.preferenceManager
    
    val searchQuery = MutableLiveData<String>("")
    val navigateToSearch = MutableLiveData<Boolean>(false)
    
    // Swipe to refresh state
    val isRefreshing = MutableLiveData<Boolean>(false)
    
    // Refresh trigger event
    val refreshEvent = MutableLiveData<Boolean>(false)

    val libraryDetails = MutableLiveData<LibraryDetail?>()
    val errorMessage = MutableLiveData<String?>()
    val isLoading = MutableLiveData<Boolean>(false)
    val unAuthenticated = MutableLiveData<Boolean>(false)

    /**
     * Fetches app settings from the API and saves them to DataStore.
     * This is called from the Splash screen in the background.
     */
    fun fetchAppSettings() {
        viewModelScope.launch {
            try {
                val response = repository.getAppSettings()
                if (response.isSuccessful && response.body()?.status == true) {
                    val settingsData = response.body()?.data
                    settingsData?.let{
                        preferenceManager.saveAppSettings(it)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Fetches library details from the API and saves them to PreferenceManager.
     */
    fun fetchLibraryDetails() {
        viewModelScope.launch {
            isLoading.value = true
            val result = authRepository.getLibraryDetails()
            isLoading.value = false
            when (result) {
                is NetworkResult.Success -> {
                    val details = result.data?.data
                    details?.let{
                        libraryDetails.value = details
                        preferenceManager.saveLibraryDetails(details)
                        LibraroApp.libraryDetail = details
                    }
                }
                is NetworkResult.Error -> {
                    errorMessage.value = result.message
                }
                is NetworkResult.Unauthorized -> {
                    errorMessage.value = result.message
                    unAuthenticated.value = true
                }
                else ->{
                    errorMessage.value = "Something went wrong"
                }
            }
        }
    }

    fun onSearchClicked() {
        val query = searchQuery.value
        if (!query.isNullOrBlank()) {
            navigateToSearch.value = true
        }
    }

    fun onNavigationHandled() {
        navigateToSearch.value = false
    }

    fun onRefresh() {
        refreshEvent.value = true
        fetchLibraryDetails()
    }

    fun onRefreshHandled() {
        refreshEvent.value = false
        isRefreshing.value = false
    }
}