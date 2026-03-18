package com.techito.libraro.ui.library.plan

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import com.techito.libraro.R
import com.techito.libraro.databinding.ActivityAddEditLibraryPlanBinding
import com.techito.libraro.utils.AppUtils

class AddEditLibraryPlanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditLibraryPlanBinding
    private var isEdit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_edit_library_plan)
        AppUtils.changeStatusBarColor(this, R.color.white, true)
        WindowCompat.setDecorFitsSystemWindows(window, true)
        AppUtils.handleSystemBars(binding.mainLayout)

        isEdit = intent.getBooleanExtra("isEdit", false)
        binding.isEdit = isEdit

        setupSpinners()
        setupListeners()
        
        // Initial setup
        val initialMonths = binding.etNoOfMonth.text.toString().toIntOrNull() ?: 1
        updateDaysOptions(initialMonths)
        toggleMonthlyDaysVisibility(binding.actvPlanName.text.toString())
    }

    private fun setupSpinners() {
        val planNames = arrayOf("MONTH", "YEAR", "DAY", "WEEK")
        val planNameAdapter =
            ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, planNames)
        binding.actvPlanName.setAdapter(planNameAdapter)
    }

    private fun toggleMonthlyDaysVisibility(planName: String) {
        if (planName == "MONTH") {
            binding.tilMonthlyDays.visibility = View.VISIBLE
        } else {
            binding.tilMonthlyDays.visibility = View.GONE
        }
    }

    private fun updateDaysOptions(months: Int) {
        val option1 = "${months * 28} Days"
        val option2 = "${months * 30} Days"
        val option3 = if (months == 1) "Automatic (Calendar Wise)" else "$months Months (Calendar Wise)"
        
        val daysOptions = arrayOf(option1, option2, option3)
        val daysAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, daysOptions)
        binding.actvMonthlyDays.setAdapter(daysAdapter)
        
        // If the current text doesn't match new options, clear it
        val currentText = binding.actvMonthlyDays.text.toString()
        if (!daysOptions.contains(currentText)) {
            binding.actvMonthlyDays.setText("", false)
        }
    }

    private fun setupListeners() {
        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.actvPlanName.setOnItemClickListener { parent, _, position, _ ->
            val selectedPlan = parent.getItemAtPosition(position).toString()
            toggleMonthlyDaysVisibility(selectedPlan)
        }

        binding.etNoOfMonth.doAfterTextChanged { text ->
            val months = text.toString().toIntOrNull()
            if (months != null && months > 0) {
                updateDaysOptions(months)
            } else {
                binding.actvMonthlyDays.setAdapter(null)
                binding.actvMonthlyDays.setText("", false)
            }
        }

        binding.btnSubmit.setOnClickListener {
            if (validate()) {
                val msg = if (isEdit) "Plan updated successfully" else "Plan added successfully"
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun validate(): Boolean {
        if (binding.actvPlanName.text.isNullOrBlank()) {
            Toast.makeText(this, "Please select plan type", Toast.LENGTH_SHORT).show()
            return false
        }
        if (binding.etNoOfMonth.text.isNullOrBlank()) {
            Toast.makeText(this, "Please enter number of months", Toast.LENGTH_SHORT).show()
            return false
        }
        if (binding.tilMonthlyDays.visibility == View.VISIBLE && binding.actvMonthlyDays.text.isNullOrBlank()) {
            Toast.makeText(this, "Please select month days", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}
