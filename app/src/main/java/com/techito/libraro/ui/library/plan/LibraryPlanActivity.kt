package com.techito.libraro.ui.library.plan

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.techito.libraro.R
import com.techito.libraro.databinding.ActivityLibraryPlanBinding
import com.techito.libraro.ui.library.plan.LibraryPlanAdapter
import com.techito.libraro.ui.library.users.AddEditLibraryUserActivity
import com.techito.libraro.utils.AppUtils

class LibraryPlanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLibraryPlanBinding
    private lateinit var planAdapter: LibraryPlanAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_library_plan)
        AppUtils.changeStatusBarColor(this, R.color.white, true)
        WindowCompat.setDecorFitsSystemWindows(window, true)
        AppUtils.handleSystemBars(binding.mainLayout)

        initRecyclerView()
        setupListeners()
        loadDummyData()
    }

    private fun initRecyclerView() {
        planAdapter = LibraryPlanAdapter(
            onEditClick = { libraryPlan, position ->
                openAddEditPlan(true)
            },
            onDeleteClick = { libraryPlan, position ->
                // Handle delete
            }
        )
        binding.rvPlans.apply {
            layoutManager = LinearLayoutManager(this@LibraryPlanActivity)
            adapter = planAdapter
        }
    }

    private fun setupListeners() {
        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.ivAddPlan.setOnClickListener {
            openAddEditPlan(false)
        }
        binding.fabAddPlan.setOnClickListener {
            binding.ivAddPlan.performClick()
        }
    }

    private fun openAddEditPlan(isEdit: Boolean) {
        val intent = Intent(this, AddEditLibraryPlanActivity::class.java)
        intent.putExtra("isEdit", isEdit)
        startActivity(intent)
    }

    private fun loadDummyData() {
        val dummyPlans = listOf(
            DummyPlan("Monthly", "1", "30"),
            DummyPlan("Quarterly", "3", "90"),
            DummyPlan("Yearly", "12", "365")
        )
        planAdapter.submitList(dummyPlans)
    }

    data class DummyPlan(val name: String, val months: String, val days: String)
}