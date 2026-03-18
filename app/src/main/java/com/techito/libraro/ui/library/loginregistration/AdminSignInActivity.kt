package com.techito.libraro.ui.library.loginregistration

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.techito.libraro.R
import com.techito.libraro.databinding.ActivityAdminSignInBinding
import com.techito.libraro.ui.library.MainActivity
import com.techito.libraro.utils.AppUtils
import com.techito.libraro.viewmodel.AdminAuthViewModel

class AdminSignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminSignInBinding
    private lateinit var viewModel: AdminAuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_admin_sign_in)
        AppUtils.changeStatusBarColor(this, R.color.white, true)

        WindowCompat.setDecorFitsSystemWindows(window, true)
        viewModel = ViewModelProvider(this)[AdminAuthViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        AppUtils.handleSystemBars(binding.mainLayout)
        
        setupSignUpText()
        setupObservers()

        binding.tvForgotPassword.setOnClickListener {
            startActivity(Intent(this, AdminForgotPasswordActivity::class.java))
        }
    }

    private fun setupObservers() {
        viewModel.navigateToHome.observe(this) { shouldNavigate ->
            if (shouldNavigate) {
                startActivity(Intent(this, MainActivity::class.java))
                viewModel.onNavigationHandled()
                finish()
            }
        }

        viewModel.navigateToOtp.observe(this) { libraryId ->
            if (!libraryId.isNullOrBlank()) {
                startActivity(
                    Intent(this, AdminOtpVerificationActivity::class.java)
                    .putExtra("library_id", libraryId)
                    .putExtra("email", viewModel.signInEmail.value))
                viewModel.onNavigationHandled()
            }
        }

        viewModel.navigateToPlanSelection.observe(this) { response ->
            if (response != null) {
                startActivity(Intent(this, AdminPlanSelectionActivity::class.java))
                viewModel.onNavigationHandled()
            }
        }
        viewModel.navigateToAddBranchAndFloor.observe(this) { response ->
            if (response) {
                startActivity(Intent(this, BranchConfigurationActivity::class.java))
                viewModel.onNavigationHandled()
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

    private fun setupSignUpText() {
        val fullText = getString(R.string.dont_have_account) + " " + getString(R.string.sign_up)
        val spannableString = SpannableString(fullText)
        val signUpPart = getString(R.string.sign_up)
        val startIndex = fullText.indexOf(signUpPart)

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                startActivity(Intent(this@AdminSignInActivity, AdminSignUpActivity::class.java))
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = getColor(R.color.dot_active)
                ds.isUnderlineText = false
                ds.isFakeBoldText = true
            }
        }

        if (startIndex != -1) {
            spannableString.setSpan(
                clickableSpan,
                startIndex,
                startIndex + signUpPart.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        binding.tvSignUp.text = spannableString
        binding.tvSignUp.movementMethod = LinkMovementMethod.getInstance()
        binding.tvSignUp.highlightColor = Color.TRANSPARENT
    }
}
