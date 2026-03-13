package com.techito.libraro.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.techito.libraro.LibraroApp
import com.techito.libraro.R
import com.techito.libraro.databinding.ActivitySplashBinding
import com.techito.libraro.utils.AppUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() 
        AppUtils.changeStatusBarColor(this, R.color.white, false)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)
        
        // Accessing preferenceManager from application class
        val preferenceManager = LibraroApp.preferenceManager

        lifecycleScope.launch {
            delay(2500)
            // Using .first() to get current value from DataStore Flow
            val isFirstTime = preferenceManager.isFirstTimeLaunch.first()
            val isLoggedIn = preferenceManager.isLoggedIn.first()
            
            when {
                isFirstTime -> {
                    startActivity(Intent(this@SplashActivity, IntroActivity::class.java))
                }
                !isLoggedIn -> {
                    startActivity(Intent(this@SplashActivity, LoginOptionActivity::class.java))
                }
                else -> {
                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                }
            }
            finish()
        }
    }
}
