package com.techito.libraro.ui

import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.text.Html
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.techito.libraro.R
import com.techito.libraro.databinding.ActivityAdminOtpVerificationBinding
import com.techito.libraro.utils.AppUtils
import com.techito.libraro.viewmodel.AdminAuthViewModel
import kotlin.getValue

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
        viewModel.startOtpTimer()
        handleInsets()
        val mobileNumber = intent.getStringExtra("mobileNumber")


        viewModel.otpTimerText.observe(this) { timerText ->
            binding.tvOtpTimer.text = Html.fromHtml(timerText, Html.FROM_HTML_MODE_LEGACY)
        }

        binding.tvOtpTimer.setOnClickListener {
            viewModel.startOtpTimer()
        }

        binding.btnVerify.setOnClickListener {
            // Implementation for OTP verification
            AppUtils.showToast(this, "OTP Verified")
            startActivity(Intent(this, AdminPlanSelectionActivity::class.java))
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
