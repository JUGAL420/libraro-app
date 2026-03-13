package com.techito.libraro

import android.app.Application
import com.techito.libraro.data.local.PreferenceManager

/**
 * Main Application class for Libraro.
 * This class is used to initialize global components and provide access to PreferenceManager.
 */
class LibraroApp : Application() {
    companion object {
        lateinit var preferenceManager: PreferenceManager
            private set
    }

    override fun onCreate() {
        super.onCreate()
        
        // Initialize PreferenceManager singleton
        preferenceManager = PreferenceManager.getInstance(this)
    }
}
