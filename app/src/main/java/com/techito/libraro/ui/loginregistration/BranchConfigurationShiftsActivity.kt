package com.techito.libraro.ui.loginregistration

import android.graphics.Rect
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.techito.libraro.R
import com.techito.libraro.databinding.ActivityBranchConfigurationShiftsBinding
import com.techito.libraro.ui.adapter.BranchConfigurationShiftAdapter
import com.techito.libraro.utils.AppUtils
import com.techito.libraro.viewmodel.BranchConfigurationViewModel
import java.util.Locale

class BranchConfigurationShiftsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBranchConfigurationShiftsBinding
    private lateinit var viewModel: BranchConfigurationViewModel
    private lateinit var shiftAdapter: BranchConfigurationShiftAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        AppUtils.changeStatusBarColor(this, R.color.white, true)

        viewModel = ViewModelProvider(this)[BranchConfigurationViewModel::class.java]
        binding = DataBindingUtil.setContentView(this, R.layout.activity_branch_configuration_shifts)
        WindowCompat.setDecorFitsSystemWindows(window, true)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        handleInsets()
        setupShiftList()
        setupObservers()

        binding.mcvAddShift.setOnClickListener {
            viewModel.addShift()
        }
    }

    private fun setupObservers() {
        // Observe Loading state
        viewModel.isLoading.observe(this) { isLoading ->
            binding.layoutProgress.clProgress.isVisible = isLoading
        }

        // Observe Error messages
        viewModel.errorMessage.observe(this) { message ->
            message?.let {
                AppUtils.showToast(this, it)
                viewModel.onErrorHandled()
            }
        }

        // Observe Success state
        viewModel.configurationSuccess.observe(this) { success ->
            if (success) {
                AppUtils.showToast(this, "Branch Configuration Saved Successfully!")
                // Navigate to next screen (e.g., Dashboard)
                // finishAffinity()
                // startActivity(Intent(this, MainActivity::class.java))
            }
        }
    }

    private fun setupShiftList() {
       /* shiftAdapter = BranchConfigurationShiftAdapter(
            viewModel.shifts.value ?: emptyList(),
            onAddClick = { viewModel.addShift() },
            onDeleteClick = { position -> viewModel.removeShift(position) },
            onTimeClick = { position, isStartTime ->
                showTimePicker(position, isStartTime)
            }
        )
        binding.rvShifts.adapter = shiftAdapter

        viewModel.shifts.observe(this) { shifts ->
            shiftAdapter.updateData(shifts)
        }*/
    }

    private fun showTimePicker(position: Int, isStartTime: Boolean) {
        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(12)
            .setMinute(0)
            .setTitleText("Select Time")
            .build()

        picker.addOnPositiveButtonClickListener {
            val hour = picker.hour
            val minute = picker.minute
            val amPm = if (hour < 12) "AM" else "PM"
            val formattedHour = if (hour % 12 == 0) 12 else hour % 12
            val time = String.format(Locale.getDefault(), "%02d:%02d %s", formattedHour, minute, amPm)
            
            val currentShifts = viewModel.shifts.value ?: return@addOnPositiveButtonClickListener
            if (isStartTime) {
                currentShifts[position].startTime = time
            } else {
                currentShifts[position].endTime = time
            }
            viewModel.shifts.value = currentShifts // Trigger update
        }

        picker.show(supportFragmentManager, "TIME_PICKER")
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
