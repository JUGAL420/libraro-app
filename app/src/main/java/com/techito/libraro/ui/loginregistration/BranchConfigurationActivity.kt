package com.techito.libraro.ui.loginregistration

import android.graphics.Rect
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
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
        handleInsets()
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
    }

    private fun handleInsets() {
        val mainLayout = binding.mainLayout
        val originalPadding = Rect(
            mainLayout.paddingLeft,
            mainLayout.paddingTop,
            mainLayout.paddingRight,
            mainLayout.paddingBottom
        )

        ViewCompat.setOnApplyWindowInsetsListener(mainLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                originalPadding.left,
                originalPadding.top + systemBars.top,
                originalPadding.right,
                originalPadding.bottom + systemBars.bottom
            )
            insets
        }
    }
}
