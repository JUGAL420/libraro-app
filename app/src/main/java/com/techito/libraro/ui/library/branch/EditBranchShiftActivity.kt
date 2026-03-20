package com.techito.libraro.ui.library.branch

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.databinding.DataBindingUtil
import com.techito.libraro.R
import com.techito.libraro.databinding.ActivityEditBranchShiftBinding
import com.techito.libraro.ui.library.adapter.BranchConfigurationShiftAdapter
import com.techito.libraro.utils.AppUtils

class EditBranchShiftActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditBranchShiftBinding
    private lateinit var shiftAdapter: BranchConfigurationShiftAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_branch_shift)

        AppUtils.changeStatusBarColor(this, R.color.white, true)
        WindowCompat.setDecorFitsSystemWindows(window, true)
        AppUtils.handleSystemBars(binding.mainLayout)

        setupListeners()
        setupShiftList()
    }

    private fun setupListeners() {
        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupShiftList() {
        /*shiftAdapter = BranchConfigurationShiftAdapter(
            viewModel.shifts.value ?: emptyList(),
            onShiftName = { position, dropDownView -> addShiftName(position, dropDownView) },
            onDeleteClick = { position -> viewModel.removeShift(position) },
            onTimeClick = { position, isStartTime ->
                showTimePicker(position, isStartTime)
            }
        )
        binding.rvShifts.adapter = shiftAdapter

        viewModel.shifts.observe(viewLifecycleOwner) { shifts ->
            shiftAdapter.updateData(shifts)
        }*/
    }
}
