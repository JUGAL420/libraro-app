package com.techito.libraro.ui.library.expense

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.databinding.DataBindingUtil
import com.techito.libraro.R
import com.techito.libraro.databinding.ActivityAddEditExpenseNameBinding
import com.techito.libraro.model.ExpenseName
import com.techito.libraro.utils.AppUtils

class AddEditExpenseNameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditExpenseNameBinding
    private var isEdit = false
    private var expense: ExpenseName? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_edit_expense_name)
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
        if (isEdit && expense != null) {
            binding.etExpenseName.setText(expense?.name)
        }
    }

    private fun setupListeners() {
        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnSubmit.setOnClickListener {
            val name = binding.etExpenseName.text.toString().trim()
            if (name.isEmpty()) {
                binding.tilExpenseName.error = "Please enter expense name"
                return@setOnClickListener
            }
            // Handle save/update logic
            finish()
        }
    }
}
