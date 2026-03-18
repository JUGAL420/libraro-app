package com.techito.libraro.ui.library.branchfloor

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.techito.libraro.R
import com.techito.libraro.databinding.ActivityBranchFloorBlockBinding
import com.techito.libraro.model.BranchConfigurationFloor
import com.techito.libraro.ui.library.branchfloor.AddEditBranchFloorActivity
import com.techito.libraro.ui.library.branchfloor.BranchFloorAdapter
import com.techito.libraro.utils.AppUtils

class BranchFloorBlockActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBranchFloorBlockBinding
    private lateinit var floorAdapter: BranchFloorAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_branch_floor_block)
        AppUtils.changeStatusBarColor(this, R.color.white, true)
        WindowCompat.setDecorFitsSystemWindows(window, true)
        AppUtils.handleSystemBars(binding.mainLayout)

        initRecyclerView()
        setupListeners()

        // Load initial data
        loadInitialData()
    }

    private fun initRecyclerView() {
        floorAdapter = BranchFloorAdapter(
            onEditClick = { floor, _ ->
                startActivity(Intent(this, AddEditBranchFloorActivity::class.java).apply {
                    putExtra("isEdit", true)
                })
            },
            onDeleteClick = { _, position ->
                val currentList = floorAdapter.currentList.toMutableList()
                currentList.removeAt(position)
                floorAdapter.submitList(currentList) {
                    updateTotalSeats(currentList)
                }
            }
        )
        binding.rvFloors.apply {
            layoutManager = LinearLayoutManager(this@BranchFloorBlockActivity)
            adapter = floorAdapter
        }
    }

    private fun setupListeners() {
        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.ivAddFloor.setOnClickListener {
            addNewFloor()
        }

        binding.fabAddFloor.setOnClickListener {
            addNewFloor()
        }
    }

    private fun addNewFloor() {
        // This would typically open a dialog or another activity
        val currentList = floorAdapter.currentList.toMutableList()
        val nextFloorNum = currentList.size + 1
        val startSeat = if (currentList.isEmpty()) 1 else (currentList.last().seatTo ?: 0) + 1
        val endSeat = startSeat + 19

        val newFloor = BranchConfigurationFloor("Floor $nextFloorNum", startSeat, endSeat)
        currentList.add(newFloor)

        floorAdapter.submitList(currentList) {
            updateTotalSeats(currentList)
            binding.rvFloors.smoothScrollToPosition(currentList.size - 1)
        }

        startActivity(Intent(this, AddEditBranchFloorActivity::class.java).apply {
            putExtra("isEdit", false)
        })
    }

    private fun loadInitialData() {
        val list = mutableListOf<BranchConfigurationFloor>()
        list.add(BranchConfigurationFloor("Ground Floor", 1, 50))
        list.add(BranchConfigurationFloor("First Floor", 51, 100))
        list.add(BranchConfigurationFloor("Second Floor", 101, 150))
        floorAdapter.submitList(list) {
            updateTotalSeats(list)
        }
    }

    private fun updateTotalSeats(list: List<BranchConfigurationFloor>) {
        val totalSeats = list.sumOf { (it.seatTo ?: 0) - (it.seatFrom ?: 0) + 1 }
        binding.tvTotalSeats.text = getString(R.string.total_seats_display, totalSeats)
    }
}