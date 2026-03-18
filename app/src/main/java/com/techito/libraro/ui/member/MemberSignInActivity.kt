package com.techito.libraro.ui.member

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.databinding.DataBindingUtil
import com.techito.libraro.R
import com.techito.libraro.databinding.ActivityMemberSignInBinding
import com.techito.libraro.utils.AppUtils

class MemberSignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMemberSignInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_member_sign_in)
        AppUtils.changeStatusBarColor(this, R.color.white, true)

        WindowCompat.setDecorFitsSystemWindows(window, true)

        AppUtils.handleSystemBars(binding.mainLayout)
    }
}