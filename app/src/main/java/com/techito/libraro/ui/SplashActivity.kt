package com.techito.libraro.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.razorpay.Checkout
import com.techito.libraro.LibraroApp
import com.techito.libraro.R
import com.techito.libraro.databinding.ActivitySplashBinding
import com.techito.libraro.ui.library.MainActivity
import com.techito.libraro.utils.AppUtils
import com.techito.libraro.viewmodel.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() 
        AppUtils.changeStatusBarColor(this, R.color.color_transparent, false)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)
        
        // Fetch app settings in the background
        viewModel.fetchAppSettings()
        Checkout.clearUserData(this)
        // Accessing preferenceManager from application class
        val preferenceManager = LibraroApp.preferenceManager

        lifecycleScope.launch {
            Log.d("SplashActivity", "User Token: ${preferenceManager.authToken.first()}")
            // Check and store device ID if not already available
            val storedDeviceId = preferenceManager.deviceId.first()
            if (storedDeviceId.isNullOrEmpty()) {
                val deviceId = AppUtils.getDeviceId(this@SplashActivity)
                preferenceManager.saveDeviceId(deviceId)
            }

            // Minimum splash delay
            delay(2500)
            
            // Using .first() to get current value from DataStore Flow
            val isFirstTime = preferenceManager.isFirstTimeLaunch.first()
            val isLoggedIn = preferenceManager.isLoggedIn.first()

            when {
                isFirstTime -> {
                    startActivity(Intent(this@SplashActivity, IntroActivity::class.java))
                }
                isLoggedIn -> {
                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                }
                else -> {
                    preferenceManager.saveAuthToken("")
                    preferenceManager.saveLibraryId("")
                    preferenceManager.saveUserType("")
                    startActivity(Intent(this@SplashActivity, LoginOptionActivity::class.java))
                }
            }
            finish()
        }
    }
}
