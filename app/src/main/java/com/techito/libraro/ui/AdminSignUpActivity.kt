package com.techito.libraro.ui

import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.techito.libraro.R
import com.techito.libraro.databinding.ActivityAdminSignUpBinding
import com.techito.libraro.utils.AppUtils
import com.techito.libraro.viewmodel.AdminAuthViewModel

class AdminSignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminSignUpBinding
    private lateinit var viewModel: AdminAuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_admin_sign_up)
        AppUtils.changeStatusBarColor(this, R.color.white, true)
        WindowCompat.setDecorFitsSystemWindows(window, true)
        viewModel = ViewModelProvider(this)[AdminAuthViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        handleInsets()
        setupSignInText()

        binding.btnProceed.setOnClickListener {
            startActivity(Intent(this, AdminOtpVerificationActivity::class.java))
        }
    }

    private fun setupSignInText() {
        val fullText = getString(R.string.already_register) +" "+ getString(R.string.sign_in)
        val spannableString = SpannableString(fullText)
        val signInPart = getString(R.string.sign_in)
        val startIndex = fullText.indexOf(signInPart)

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                startActivity(Intent(this@AdminSignUpActivity, AdminSignInActivity::class.java))
                finish()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = getColor(R.color.app_color_1)
                ds.isUnderlineText = false
                ds.isFakeBoldText = true
            }
        }

        spannableString.setSpan(
            clickableSpan,
            startIndex,
            startIndex + signInPart.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.tvSignIn.text = spannableString
        binding.tvSignIn.movementMethod = LinkMovementMethod.getInstance()
        binding.tvSignIn.highlightColor = Color.TRANSPARENT
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
