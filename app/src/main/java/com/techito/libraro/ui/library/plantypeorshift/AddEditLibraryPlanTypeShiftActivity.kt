package com.techito.libraro.ui.library.plantypeorshift

import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.databinding.DataBindingUtil
import com.techito.libraro.R
import com.techito.libraro.databinding.ActivityAddEditLibraryPlanTypeShiftBinding
import com.techito.libraro.utils.AppUtils
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddEditLibraryPlanTypeShiftActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditLibraryPlanTypeShiftBinding
    private var isEdit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_edit_library_plan_type_shift)

        AppUtils.changeStatusBarColor(this, R.color.white, true)
        WindowCompat.setDecorFitsSystemWindows(window, true)
        AppUtils.handleSystemBars(binding.mainLayout)

        isEdit = intent.getBooleanExtra("isEdit", false)
        binding.isEdit = isEdit

        setupSpinners()
        setupListeners()
    }

    private fun setupSpinners() {
        val shiftNames = arrayOf("Full Day", "Shift A", "Shift B", "Shift C", "Custom")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, shiftNames)
        binding.actvShiftName.setAdapter(adapter)
    }

    private fun setupListeners() {
        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.etStartTime.setOnClickListener {
            AppUtils.showTimePicker(supportFragmentManager, "Select Start Time"){ formattedTime ->
                binding.etStartTime.setText(formattedTime)
                calculateDuration()

            }
        }

        binding.etEndTime.setOnClickListener {
            AppUtils.showTimePicker(supportFragmentManager, "Select End Time"){ formattedTime ->
                binding.etEndTime.setText(formattedTime)
                calculateDuration()

            }
        }

        binding.btnSubmit.setOnClickListener {
            if (validate()) {
                val msg = if (isEdit) "Shift updated successfully" else "Shift added successfully"
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun showTimePicker(onTimeSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        TimePickerDialog(this, { _, selectedHour, selectedMinute ->
            val selectedTime = Calendar.getInstance()
            selectedTime.set(Calendar.HOUR_OF_DAY, selectedHour)
            selectedTime.set(Calendar.MINUTE, selectedMinute)
            val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
            onTimeSelected(sdf.format(selectedTime.time))
        }, hour, minute, false).show()
    }

    private fun calculateDuration() {
        val startStr = binding.etStartTime.text.toString()
        val endStr = binding.etEndTime.text.toString()

        if (startStr.isNotEmpty() && endStr.isNotEmpty()) {
            try {
                val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
                val startDate = sdf.parse(startStr)
                val endDate = sdf.parse(endStr)

                if (startDate != null && endDate != null) {
                    var diff = endDate.time - startDate.time
                    if (diff < 0) {
                        diff += 24 * 60 * 60 * 1000 // Handle overnight shifts
                    }
                    val hours = diff / (1000 * 60 * 60).toDouble()
                    binding.etDuration.setText(String.Companion.format(Locale.getDefault(), "%.1f Hour", hours))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun validate(): Boolean {
        if (binding.actvShiftName.text.isNullOrBlank()) {
            Toast.makeText(this, "Please select shift name", Toast.LENGTH_SHORT).show()
            return false
        }
        if (binding.etStartTime.text.isNullOrBlank()) {
            Toast.makeText(this, "Please select start time", Toast.LENGTH_SHORT).show()
            return false
        }
        if (binding.etEndTime.text.isNullOrBlank()) {
            Toast.makeText(this, "Please select end time", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}