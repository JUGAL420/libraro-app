package com.techito.libraro.ui.library.planprice

import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.databinding.DataBindingUtil
import com.techito.libraro.R
import com.techito.libraro.databinding.ActivityAddEditLibraryPlanPriceBinding
import com.techito.libraro.ui.library.planprice.LibraryPlanPriceActivity
import com.techito.libraro.utils.AppUtils

class AddEditLibraryPlanPriceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditLibraryPlanPriceBinding
    private var isEdit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_edit_library_plan_price)

        AppUtils.changeStatusBarColor(this, R.color.white, true)
        WindowCompat.setDecorFitsSystemWindows(window, true)
        AppUtils.handleSystemBars(binding.mainLayout)

        isEdit = intent.getBooleanExtra("isEdit", false)
        binding.isEdit = isEdit


        setupSpinners()
        setupListeners()
    }

    private fun setupSpinners() {
        val planNames = arrayOf("Monthly", "Quarterly", "Half Yearly", "Yearly")
        val planAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, planNames)
        binding.actvPlanName.setAdapter(planAdapter)

        val planTypes = arrayOf("Full Day", "Shift A", "Shift B", "Shift C")
        val typeAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, planTypes)
        binding.actvPlanType.setAdapter(typeAdapter)
    }

    private fun setupListeners() {
        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnSubmit.setOnClickListener {
            if (validate()) {
                val msg = if (isEdit) "Plan price updated successfully" else "Plan price added successfully"
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun validate(): Boolean {
        if (binding.actvPlanName.text.isNullOrBlank()) {
            Toast.makeText(this, "Please select plan name", Toast.LENGTH_SHORT).show()
            return false
        }
        if (binding.actvPlanType.text.isNullOrBlank()) {
            Toast.makeText(this, "Please select plan type", Toast.LENGTH_SHORT).show()
            return false
        }
        if (binding.etPlanPrice.text.isNullOrBlank()) {
            Toast.makeText(this, "Please enter plan price", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}