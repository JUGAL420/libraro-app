package com.techito.libraro.ui

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.text.Html
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
import com.techito.libraro.databinding.ActivityAdminOtpVerificationBinding
import com.techito.libraro.utils.AppUtils
import com.techito.libraro.viewmodel.AdminAuthViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AdminOtpVerificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminOtpVerificationBinding
    private val viewModel: AdminAuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        AppUtils.changeStatusBarColor(this, R.color.white, true)
        WindowCompat.setDecorFitsSystemWindows(window, true)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_admin_otp_verification)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        val libraryId = intent.getStringExtra("library_id") ?: ""
        val email = intent.getStringExtra("email") ?: ""

        viewModel.libraryId.value = libraryId
        viewModel.userEmail.value = email

        handleInsets()
        setupDescription(email)
        viewModel.startOtpTimer()
        setupObservers()

        binding.btnVerify.setOnClickListener {
            viewModel.onVerifyOtpClicked()
        }
    }

    private fun setupDescription(email: String) {
        // Dynamic email replacement with bold styling
        val message = getString(R.string.otp_sent_msg)
        val fullText = message.replace("{email}", "<b>$email</b>")
        binding.tvDescription.text = Html.fromHtml(fullText, Html.FROM_HTML_MODE_LEGACY)
        
        // Dynamic support contact number replacement from PreferenceManager
        lifecycleScope.launch {
            val appSettings = LibraroApp.preferenceManager.appSettings.first()
            val contactNo = appSettings?.contactNumber?.firstOrNull() ?: "+91 81144 79678"
            val supportMessage = getString(R.string.need_help_msg).replace("{contact_no}", contactNo)
            binding.tvNeedHelp.text = supportMessage
        }
    }

    private fun setupObservers() {
        viewModel.otpTimerText.observe(this) { timerText ->
            binding.tvOtpTimer.text = Html.fromHtml(timerText, Html.FROM_HTML_MODE_LEGACY)
        }

        viewModel.navigateToPlanSelection.observe(this) { otpVerifyResponse ->
            if (otpVerifyResponse?.status == true) {
                otpVerifyResponse.data?.libraryId?.let{
                    startActivity(Intent(this, AdminPlanSelectionActivity::class.java))
                    viewModel.onNavigationHandled()
                    finish()
                }
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
