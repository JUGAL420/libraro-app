package com.techito.libraro.ui.library.branch

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.databinding.DataBindingUtil
import com.techito.libraro.R
import com.techito.libraro.databinding.ActivityAddEditLibraryBranchBinding
import com.techito.libraro.utils.AppUtils

class AddEditLibraryBranchActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditLibraryBranchBinding
    private var isEdit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_edit_library_branch)

        AppUtils.changeStatusBarColor(this, R.color.white, true)
        WindowCompat.setDecorFitsSystemWindows(window, true)
        AppUtils.handleSystemBars(binding.mainLayout)

        isEdit = intent.getBooleanExtra("isEdit", false)
        binding.isEdit = isEdit

        setupListeners()
    }

    private fun setupListeners() {
        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}
