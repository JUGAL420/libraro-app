package com.techito.libraro.ui.library.branch

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.techito.libraro.R
import com.techito.libraro.databinding.ActivityLibraryBranchMasterBinding
import com.techito.libraro.utils.AppUtils
import java.io.Serializable

class LibraryBranchMasterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLibraryBranchMasterBinding
    private lateinit var adapter: LibraryBranchAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_library_branch_master)

        AppUtils.changeStatusBarColor(this, R.color.white, true)
        WindowCompat.setDecorFitsSystemWindows(window, true)
        AppUtils.handleSystemBars(binding.mainLayout)

        initRecyclerView()
        setupListeners()
        loadDummyData()
    }

    private fun initRecyclerView() {
        adapter = LibraryBranchAdapter(
            onEditClick = { branch, _ ->
                val intent = Intent(this, AddEditLibraryBranchActivity::class.java)
                intent.putExtra("isEdit", true)
                intent.putExtra("branch", branch)
                startActivity(intent)
            },
            onBookingClick = { branch, _ ->
                Toast.makeText(this, "Booking ${branch.name}", Toast.LENGTH_SHORT).show()
            }
        )
        binding.rvBranches.apply {
            layoutManager = LinearLayoutManager(this@LibraryBranchMasterActivity)
            this.adapter = this@LibraryBranchMasterActivity.adapter
        }
    }

    private fun setupListeners() {
        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.ivAddBranch.setOnClickListener {
            startAddBranchActivity()
        }
        
        binding.fabAddBranch.setOnClickListener {
            startAddBranchActivity()
        }
    }

    private fun startAddBranchActivity() {
        val intent = Intent(this, AddEditLibraryBranchActivity::class.java)
        intent.putExtra("isEdit", false)
        startActivity(intent)
    }

    private fun loadDummyData() {
        val dummyData = listOf(
            DummyBranch(
                "VINAYK LIBRARY",
                "+91-8114479678",
                "info@gmail.com",
                "955, VINOBA BHAVE NAGAR KOTA, RAJASTAHAN 324005"
            )
        )
        adapter.submitList(dummyData)
    }

    data class DummyBranch(
        val name: String,
        val mobile: String,
        val email: String,
        val address: String
    ) : Serializable
}
