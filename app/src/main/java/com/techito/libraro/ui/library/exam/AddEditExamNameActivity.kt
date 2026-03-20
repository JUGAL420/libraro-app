package com.techito.libraro.ui.library.exam

import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.databinding.DataBindingUtil
import com.techito.libraro.R
import com.techito.libraro.databinding.ActivityAddEditExamNameBinding
import com.techito.libraro.model.ExamName
import com.techito.libraro.utils.AppUtils

class AddEditExamNameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditExamNameBinding
    private var isEdit = false
    private var exam: ExamName? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_edit_exam_name)
        AppUtils.changeStatusBarColor(this, R.color.white, true)
        WindowCompat.setDecorFitsSystemWindows(window, true)
        AppUtils.handleSystemBars(binding.mainLayout)

        isEdit = intent.getBooleanExtra("isEdit", false)

        binding.isEdit = isEdit
        binding.lifecycleOwner = this

        setupUI()
        setupListeners()
    }

    private fun setupUI() {
        if (isEdit && exam != null) {
            binding.etExamName.setText(exam?.name)
        }
    }

    private fun setupListeners() {
        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnSubmit.setOnClickListener {
            val name = binding.etExamName.text.toString().trim()
            if (name.isEmpty()) {
                binding.tilExamName.error = "Please enter exam name"
                return@setOnClickListener
            }
            // Handle save/update logic
            finish()
        }
    }
}
