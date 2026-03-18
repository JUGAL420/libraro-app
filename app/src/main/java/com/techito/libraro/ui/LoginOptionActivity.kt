package com.techito.libraro.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.techito.libraro.R
import com.techito.libraro.databinding.ActivityLoginOptionBinding
import com.techito.libraro.ui.library.LibraryProfileActivity
import com.techito.libraro.ui.library.loginregistration.AdminSignInActivity
import com.techito.libraro.ui.member.MemberSignInActivity
import com.techito.libraro.utils.AppUtils

class LoginOptionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginOptionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        AppUtils.changeStatusBarColor(this, R.color.white, true)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login_option)
        
        AppUtils.handleSystemBars(binding.mainLayout)

        binding.btnAdminLogin.setOnClickListener {
//            startActivity(Intent(this, AdminSignInActivity::class.java))
            startActivity(Intent(this, LibraryProfileActivity::class.java))
        }
        binding.btnMemberLogin.setOnClickListener {
            startActivity(Intent(this, MemberSignInActivity::class.java))
        }
    }
}
