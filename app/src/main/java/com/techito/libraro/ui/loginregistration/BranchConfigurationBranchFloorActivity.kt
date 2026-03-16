package com.techito.libraro.ui.loginregistration

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.techito.libraro.R
import com.techito.libraro.databinding.ActivityBranchConfigurationBranchFloorBinding
import com.techito.libraro.model.BranchConfigurationPlan
import com.techito.libraro.model.StaticMonthlyOption
import com.techito.libraro.ui.adapter.BranchConfigurationFloorAdapter
import com.techito.libraro.utils.AppUtils
import com.techito.libraro.viewmodel.BranchConfigurationViewModel

class BranchConfigurationBranchFloorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBranchConfigurationBranchFloorBinding
    private lateinit var viewModel: BranchConfigurationViewModel
    private lateinit var floorAdapter: BranchConfigurationFloorAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_branch_configuration_branch_floor
        )
        AppUtils.changeStatusBarColor(this, R.color.white, true)
        viewModel = ViewModelProvider(this)[BranchConfigurationViewModel::class.java]
        WindowCompat.setDecorFitsSystemWindows(window, true)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        handleInsets()
        setupFloorList()
        setupNavigationObserver()
        setupDatePicker()
        setupObservers()

        binding.mcvAddFloor.setOnClickListener {
            viewModel.addFloor()
        }
        binding.btnSaveNext.setOnClickListener {
            viewModel.onSaveAndNextClicked()
        }
    }

    private fun setupObservers() {
        // Observe static data from API to populate dropdowns
        viewModel.staticData.observe(this) { response ->
            response?.data?.let { data ->
                // 1. Populate Operating Hours (10 to 24)
                val operatingHrsList = (10..24).map { it.toString() }
                val opAdapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_dropdown_item_1line,
                    operatingHrsList
                )
                binding.actvOperatingHrs.setAdapter(opAdapter)

                // 2. Populate Plan Days from Static Data (Monthly Options)
                val monthlyOptions = data.monthlyOptions?.filterNotNull() ?: emptyList()
                viewModel.monthlyOptions.value = monthlyOptions as MutableList<StaticMonthlyOption>

                val planDaysLabels =
                    monthlyOptions.map { it.label ?: "" }.filter { it.isNotEmpty() }
                viewModel.planDaysLabels.value = planDaysLabels
                val planDaysAdapter =
                    ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, planDaysLabels)
                binding.actvPlanDays.setAdapter(planDaysAdapter)

                // Set initial values without triggering filtering
                if (viewModel.operatingHrs.value.isNullOrEmpty()) {
                    binding.actvOperatingHrs.setText("10", false)
                    viewModel.operatingHrs.value = "10"
                } else {
                    binding.actvOperatingHrs.setText(viewModel.operatingHrs.value, false)
                }

                if (planDaysLabels.isNotEmpty() && binding.actvPlanDays.text.isNullOrEmpty()) {
                    binding.actvPlanDays.setText(planDaysLabels[0], false)
                }

            }
        }

        // Handle dropdown item clicks to update ViewModel
        binding.actvOperatingHrs.setOnItemClickListener { _, _, position, _ ->
            val hours = (10..24).toList()[position].toString()
            viewModel.operatingHrs.value = hours
        }

        binding.actvPlanDays.setOnItemClickListener { _, _, position, _ ->
            val selectedPlanData =
                viewModel.monthlyOptions.value?.find {
                    it.label == viewModel.planDaysLabels.value?.get(
                        position
                    )
                }
            selectedPlanData?.let {
                viewModel.planDays.value = BranchConfigurationPlan(it.value, "1 MONTH")
            }
        }

        // Observe Loading state
        viewModel.isLoading.observe(this) { isLoading ->
            binding.layoutProgress.clProgress.isVisible = isLoading
        }

        // Observe Error messages
        viewModel.errorMessage.observe(this) { message ->
            message?.let {
                AppUtils.showToast(this, it)
                viewModel.onErrorHandled()
            }
        }
    }

    private fun setupDatePicker() {
        binding.etFoundersDay.setOnClickListener {
            AppUtils.showDatePicker(
                supportFragmentManager,
                "Select Founder's Day"
            ) { selectedDate ->
                viewModel.founderDay.value = selectedDate
            }
        }
    }

    private fun setupNavigationObserver() {
        viewModel.navigateToAddShifts.observe(this) { shouldNavigate ->
            if (shouldNavigate) {
                startActivity(Intent(this, BranchConfigurationShiftsActivity::class.java))
                viewModel.onNavigationHandled()
            }
        }
    }

    private fun setupFloorList() {
        floorAdapter = BranchConfigurationFloorAdapter(
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
