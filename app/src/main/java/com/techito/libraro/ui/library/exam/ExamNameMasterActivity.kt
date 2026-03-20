package com.techito.libraro.ui.library.exam

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.databinding.DataBindingUtil
import com.techito.libraro.R
import com.techito.libraro.databinding.ActivityExamNameMasterBinding
import com.techito.libraro.model.ExamName
import com.techito.libraro.utils.AppUtils

class ExamNameMasterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExamNameMasterBinding
    private lateinit var adapter: ExamNameAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_exam_name_master)

        AppUtils.changeStatusBarColor(this, R.color.white, true)
        WindowCompat.setDecorFitsSystemWindows(window, true)
        AppUtils.handleSystemBars(binding.mainLayout)

        initRecyclerView()
        setupListeners()
        loadDummyData()
    }

    private fun initRecyclerView() {
        adapter = ExamNameAdapter(
            onEditClick = { exam ->
                openAddEditExam(true)
            },
            onDeleteClick = { exam ->
                // Handle delete
            }
        )
        binding.rvExamNames.adapter = adapter
    }

    private fun setupListeners() {
        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.ivAddExam.setOnClickListener {
            openAddEditExam(false)
        }
    }

    private fun openAddEditExam(isEdit: Boolean) {
        val intent = Intent(this, AddEditExamNameActivity::class.java)
        intent.putExtra("isEdit", isEdit)
        startActivity(intent)
    }

    private fun loadDummyData() {
        val dummyData = listOf(
            ExamName(1, "COMPUTER ANUDESHAK"),
            ExamName(2, "RPSC"),
            ExamName(3, "GRADE 4"),
            ExamName(4, "SSC CGL"),
            ExamName(5, "BANK PO")
        )
        adapter.submitList(dummyData)
    }
}
