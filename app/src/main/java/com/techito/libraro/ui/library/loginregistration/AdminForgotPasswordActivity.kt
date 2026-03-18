package com.techito.libraro.ui.library.loginregistration

import android.graphics.Rect
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.techito.libraro.LibraroApp
import com.techito.libraro.R
import com.techito.libraro.databinding.ActivityAdminForgotPasswordBinding
import com.techito.libraro.utils.AppUtils
import com.techito.libraro.viewmodel.AdminAuthViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AdminForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminForgotPasswordBinding
    private val viewModel: AdminAuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        AppUtils.changeStatusBarColor(this, R.color.white, true)

        WindowCompat.setDecorFitsSystemWindows(window, true)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_admin_forgot_password)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        AppUtils.handleSystemBars(binding.mainLayout)
        setupObservers()
        // Dynamic support contact number replacement from PreferenceManager
        lifecycleScope.launch {
            val appSettings = LibraroApp.Companion.preferenceManager.appSettings.first()
            val contactNo = appSettings?.contactNumber?.firstOrNull() ?: "+91 81144 79678"
            val supportMessage =
                getString(R.string.need_help_msg).replace("{contact_no}", contactNo)
            binding.tvNeedHelp.text = supportMessage
        }
        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.tvBackToSignIn.setOnClickListener {
            finish()
        }
    }

    private fun setupObservers() {
        viewModel.navigateBack.observe(this) { message ->
            if (message != null) {
                AppUtils.showToast(this, message)
                viewModel.onNavigationHandled()
                finish()
            }
        }

        viewModel.errorMessage.observe(this) { message ->
            message?.let {
                AppUtils.showToast(this, it)
                viewModel.onErrorHandled()
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.layoutProgress.clProgress.isVisible = isLoading
        }
    }

}