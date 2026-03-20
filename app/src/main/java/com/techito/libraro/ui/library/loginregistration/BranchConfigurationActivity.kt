package com.techito.libraro.ui.library.loginregistration

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.techito.libraro.LibraroApp
import com.techito.libraro.R
import com.techito.libraro.databinding.ActivityBranchConfigurationBinding
import com.techito.libraro.utils.AppUtils
import com.techito.libraro.viewmodel.BranchConfigurationViewModel

class BranchConfigurationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBranchConfigurationBinding
    private lateinit var navController: NavController
    private val viewModel: BranchConfigurationViewModel by viewModels()

    private val backPressedCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_branch_configuration)
        AppUtils.changeStatusBarColor(this, R.color.white, true)
        WindowCompat.setDecorFitsSystemWindows(window, true)
        
        // Apply dynamic insets handling to the root layout
        AppUtils.handleSystemBars(binding.mainLayout)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_branch_config) as NavHostFragment
        navController = navHostFragment.navController
        onBackPressedDispatcher.addCallback(this, backPressedCallback)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            backPressedCallback.isEnabled = destination.id == R.id.configurationBranchFloorFragment
        }

        setupObservers()

    }


    private fun setupObservers() {
        // Observe Error messages
        viewModel.errorMessage.observe(this) { message ->
            message?.let {
                AppUtils.showToast(this, it)
                viewModel.onErrorHandled()
            }
        }
        viewModel.unAuthenticated.observe(this) { unAuthenticated ->
            if(unAuthenticated){
                LibraroApp.logout(this@BranchConfigurationActivity)
            }
        }
    }
}
