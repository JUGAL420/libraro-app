package com.techito.libraro.ui.library.planprice

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.techito.libraro.R
import com.techito.libraro.databinding.ActivityLibraryPlanPriceBinding
import com.techito.libraro.ui.library.planprice.LibraryPlanPriceAdapter
import com.techito.libraro.ui.library.plan.AddEditLibraryPlanActivity
import com.techito.libraro.utils.AppUtils
import java.io.Serializable

class LibraryPlanPriceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLibraryPlanPriceBinding
    private lateinit var adapter: LibraryPlanPriceAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_library_plan_price)
        AppUtils.changeStatusBarColor(this, R.color.white, true)
        WindowCompat.setDecorFitsSystemWindows(window, true)
        AppUtils.handleSystemBars(binding.mainLayout)

        initRecyclerView()
        setupListeners()
        loadDummyData()
    }

    private fun initRecyclerView() {
        adapter = LibraryPlanPriceAdapter(
            onEditClick = { planPrice, _ ->
                openAddEditPlanPrice(true)
            },
            onDeleteClick = { _, _ ->
                // Handle delete
            }
        )
        binding.rvPlanPrices.apply {
            layoutManager = LinearLayoutManager(this@LibraryPlanPriceActivity)
            adapter = this@LibraryPlanPriceActivity.adapter
        }
    }

    private fun setupListeners() {
        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.ivAddPlanPrice.setOnClickListener {
            openAddEditPlanPrice(false)
        }

        binding.fabAddPlanPrice.setOnClickListener {
            binding.ivAddPlanPrice.performClick()
        }
    }


    private fun openAddEditPlanPrice(isEdit: Boolean) {
        val intent = Intent(this, AddEditLibraryPlanPriceActivity::class.java)
        intent.putExtra("isEdit", isEdit)
        startActivity(intent)
    }


    private fun loadDummyData() {
        val dummyData = listOf(
            DummyPlanPrice("Monthly", "Full Day", "1100"),
            DummyPlanPrice("Quarterly", "Shift A", "3000")
        )
        adapter.submitList(dummyData)
    }

    data class DummyPlanPrice(
        val planName: String,
        val planType: String,
        val price: String
    ) : Serializable
}