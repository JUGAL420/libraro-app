package com.techito.libraro.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    
    val searchQuery = MutableLiveData<String>("")
    val navigateToSearch = MutableLiveData<Boolean>(false)
    
    // Swipe to refresh state
    val isRefreshing = MutableLiveData<Boolean>(false)
    
    // Refresh trigger event
    val refreshEvent = MutableLiveData<Boolean>(false)

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
    }

    fun onRefreshHandled() {
        refreshEvent.value = false
        isRefreshing.value = false
    }
}