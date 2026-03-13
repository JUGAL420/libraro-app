package com.techito.libraro.ui

import android.content.Intent
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
import com.techito.libraro.databinding.ActivityAddBranchFloorBinding
import com.techito.libraro.ui.adapter.FloorAdapter
import com.techito.libraro.utils.AppUtils
import com.techito.libraro.viewmodel.LibrarySetupViewModel

class AddBranchFloorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddBranchFloorBinding
    private lateinit var viewModel: LibrarySetupViewModel
    private lateinit var floorAdapter: FloorAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        AppUtils.changeStatusBarColor(this, R.color.white, true)
        viewModel = ViewModelProvider(this)[LibrarySetupViewModel::class.java]
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_branch_floor)
        WindowCompat.setDecorFitsSystemWindows(window, true)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        handleInsets()
        setupFloorList()
        setupNavigationObserver()
        binding.mcvAddFloor.setOnClickListener {
            viewModel.addFloor()
        }
        binding.btnSaveNext.setOnClickListener {
            viewModel.addFloor()
        }
    }

    private fun setupNavigationObserver() {
        viewModel.navigateToAddShifts.observe(this) { shouldNavigate ->
            if (shouldNavigate) {
                startActivity(Intent(this, AddShiftsActivity::class.java))
                viewModel.onNavigationHandled()
            }
        }
    }

    private fun setupFloorList() {

        floorAdapter = FloorAdapter(
            viewModel.floors.value ?: emptyList(),
            onDeleteClick = { position -> viewModel.removeFloor(position) }
        )
        binding.rvFloors.adapter = floorAdapter

        viewModel.floors.observe(this) { floors ->
            floorAdapter.updateData(floors)
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
