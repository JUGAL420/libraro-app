package com.techito.libraro.ui.library.expense

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.databinding.DataBindingUtil
import com.techito.libraro.R
import com.techito.libraro.databinding.ActivityExpenseNameMasterBinding
import com.techito.libraro.model.ExpenseName
import com.techito.libraro.ui.library.plan.AddEditLibraryPlanActivity
import com.techito.libraro.utils.AppUtils

class ExpenseNameMasterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExpenseNameMasterBinding
    private lateinit var adapter: ExpenseNameAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_expense_name_master)

        AppUtils.changeStatusBarColor(this, R.color.white, true)
        WindowCompat.setDecorFitsSystemWindows(window, true)
        AppUtils.handleSystemBars(binding.mainLayout)

        initRecyclerView()
        setupListeners()
        loadDummyData()
    }

    private fun initRecyclerView() {
        adapter = ExpenseNameAdapter(
            onEditClick = { expense ->
                openAddEditExpense(true)
            },
            onDeleteClick = { expense ->
                // Handle delete
            }
        )
        binding.rvExpenseNames.adapter = adapter
    }

    private fun setupListeners() {
        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.ivAddExpense.setOnClickListener {
            openAddEditExpense(false)
        }
    }

    private fun openAddEditExpense(isEdit: Boolean) {
        val intent = Intent(this, AddEditExpenseNameActivity::class.java)
        intent.putExtra("isEdit", isEdit)
        startActivity(intent)
    }

    private fun loadDummyData() {
        val dummyData = listOf(
            ExpenseName(1, "ELECTRICITY BILL"),
            ExpenseName(2, "WATER BILL"),
            ExpenseName(3, "RENT"),
            ExpenseName(4, "INTERNET BILL"),
            ExpenseName(5, "MAINTENANCE")
        )
        adapter.submitList(dummyData)
    }
}
