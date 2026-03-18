package com.techito.libraro.ui.library.plantypeorshift

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.techito.libraro.R
import com.techito.libraro.databinding.ActivityLibraryPlanTypeShiftBinding
import com.techito.libraro.ui.library.plantypeorshift.LibraryPlanTypeShiftAdapter
import com.techito.libraro.ui.library.plan.AddEditLibraryPlanActivity
import com.techito.libraro.ui.library.plantypeorshift.AddEditLibraryPlanTypeShiftActivity
import com.techito.libraro.utils.AppUtils

class LibraryPlanTypeShiftActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLibraryPlanTypeShiftBinding
    private lateinit var adapter: LibraryPlanTypeShiftAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_library_plan_type_shift)

        AppUtils.changeStatusBarColor(this, R.color.white, true)
        WindowCompat.setDecorFitsSystemWindows(window, true)
        AppUtils.handleSystemBars(binding.mainLayout)

        initRecyclerView()
        setupListeners()
        loadDummyData()
    }

    private fun initRecyclerView() {
        adapter = LibraryPlanTypeShiftAdapter(
            onEditClick = { shift, _ ->
                openAddEditPlanType(true)
            },
            onDeleteClick = { _, _ ->
                // Handle delete
            }
        )
        binding.rvPlanTypeShifts.apply {
            layoutManager = LinearLayoutManager(this@LibraryPlanTypeShiftActivity)
            adapter = this@LibraryPlanTypeShiftActivity.adapter
        }
    }

    private fun setupListeners() {
        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.ivAddPlanType.setOnClickListener {
            openAddEditPlanType(false)
        }

        binding.fabAddPlanType.setOnClickListener {
            binding.ivAddPlanType.performClick()
        }
    }

    private fun openAddEditPlanType(isEdit: Boolean) {
        val intent = Intent(this, AddEditLibraryPlanTypeShiftActivity::class.java)
        intent.putExtra("isEdit", isEdit)
        startActivity(intent)
    }

    private fun loadDummyData() {
        val dummyData = listOf(
            DummyShift("Full Day", "06:00 PM", "02:00 PM", "8"),
            DummyShift("Shift A : 10:00 AM to 02:00 PM", "06:00 PM", "02:00 PM", "8")
        )
        adapter.submitList(dummyData)
    }

    data class DummyShift(
        val name: String,
        val startTime: String,
        val endTime: String,
        val duration: String
    )
}