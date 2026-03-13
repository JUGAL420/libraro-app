package com.techito.libraro.ui

import android.graphics.Rect
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.techito.libraro.R
import com.techito.libraro.databinding.ActivityAddShiftsBinding
import com.techito.libraro.ui.adapter.ShiftAdapter
import com.techito.libraro.utils.AppUtils
import com.techito.libraro.viewmodel.LibrarySetupViewModel

class AddShiftsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddShiftsBinding
    private lateinit var viewModel: LibrarySetupViewModel
    private lateinit var shiftAdapter: ShiftAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        AppUtils.changeStatusBarColor(this, R.color.white, true)

        viewModel = ViewModelProvider(this)[LibrarySetupViewModel::class.java]
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_shifts)
        WindowCompat.setDecorFitsSystemWindows(window, true)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        handleInsets()
        setupShiftList()

        binding.mcvAddShift.setOnClickListener {
            viewModel.addShift()
        }
    }

    private fun setupShiftList() {
        shiftAdapter = ShiftAdapter(
            viewModel.shifts.value ?: emptyList(),
            onAddClick = { viewModel.addShift() },
            onDeleteClick = { position -> viewModel.removeShift(position) },
            onTimeClick = { position, isStartTime ->
                // Implementation for time picker
            }
        )
        binding.rvShifts.adapter = shiftAdapter

        viewModel.shifts.observe(this) { shifts ->
            shiftAdapter.updateData(shifts)
        }
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
