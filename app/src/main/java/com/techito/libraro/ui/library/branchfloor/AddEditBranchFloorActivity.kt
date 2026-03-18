package com.techito.libraro.ui.library.branchfloor

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import com.techito.libraro.R
import com.techito.libraro.databinding.ActivityAddEditBranchFloorBinding
import com.techito.libraro.utils.AppUtils

class AddEditBranchFloorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditBranchFloorBinding
    private var isEdit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_edit_branch_floor)

        AppUtils.changeStatusBarColor(this, R.color.white, true)
        WindowCompat.setDecorFitsSystemWindows(window, true)
        AppUtils.handleSystemBars(binding.mainLayout)

        isEdit = intent.getBooleanExtra("isEdit", false)
        binding.isEdit = isEdit

        setupListeners()
        calculateTotalSeats()
    }


    private fun setupListeners() {
        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.etFromSeat.doAfterTextChanged { calculateTotalSeats() }
        binding.etToSeat.doAfterTextChanged { calculateTotalSeats() }

        binding.btnSubmit.setOnClickListener {
            validateAndSave()
        }
    }

    private fun calculateTotalSeats() {
        val from = binding.etFromSeat.text.toString().toIntOrNull() ?: 0
        val to = binding.etToSeat.text.toString().toIntOrNull() ?: 0

        if (to >= from && from > 0) {
            val total = to - from + 1
            binding.etTotalSeat.setText(total.toString())
        } else {
            binding.etTotalSeat.setText("0")
        }
    }

    private fun validateAndSave() {
        val floorName = binding.etFloorName.text.toString().trim()
        val fromSeat = binding.etFromSeat.text.toString().toIntOrNull()
        val toSeat = binding.etToSeat.text.toString().toIntOrNull()

        if (floorName.isEmpty()) {
            showToast("Please enter floor name")
            return
        }
        if (fromSeat == null || toSeat == null) {
            showToast("Please enter seat range")
            return
        }
        if (fromSeat > toSeat) {
            showToast("Invalid seat range")
            return
        }
        finish()
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}