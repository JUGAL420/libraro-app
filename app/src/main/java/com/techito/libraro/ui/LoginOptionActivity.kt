package com.techito.libraro.ui

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.techito.libraro.R
import com.techito.libraro.databinding.ActivityLoginOptionBinding
import com.techito.libraro.utils.AppUtils

class LoginOptionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginOptionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        AppUtils.changeStatusBarColor(this, R.color.white, true)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login_option)
        handleInsets()
        binding.btnAdminLogin.setOnClickListener {
            startActivity(Intent(this, AdminSignInActivity::class.java))
        }
        binding.btnMemberLogin.setOnClickListener {
            startActivity(Intent(this, MemberSignInActivity::class.java))
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
