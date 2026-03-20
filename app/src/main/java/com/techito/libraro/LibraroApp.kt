package com.techito.libraro

import android.app.Activity
import android.app.Application
import android.content.Intent
import com.techito.libraro.data.local.PreferenceManager
import com.techito.libraro.model.LibraryDetail
import com.techito.libraro.ui.LoginOptionActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Main Application class for Libraro.
 * This class is used to initialize global components and provide access to PreferenceManager.
 */
class LibraroApp : Application() {
    companion object {
        lateinit var instance: LibraroApp
            private set
        lateinit var preferenceManager: PreferenceManager
            private set
            
        var libraryDetail: LibraryDetail? = null

        /**
         * Clears user data and redirects to the Login screen.
         *
         * @param activity The current activity context.
         */
        fun logout(activity: Activity) {
            CoroutineScope(Dispatchers.IO).launch {
                preferenceManager.clearAll()
                libraryDetail = null
                activity.runOnUiThread {
                    val intent = Intent(activity, LoginOptionActivity::class.java)
                    activity.startActivity(intent)
                    activity.finishAffinity()
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        
        // Initialize PreferenceManager singleton
        preferenceManager = PreferenceManager.getInstance(this)
        
        // Load library details from preferences into memory
        CoroutineScope(Dispatchers.IO).launch {
            preferenceManager.libraryDetails.collect { details ->
                libraryDetail = details
            }
        }
    }
}
