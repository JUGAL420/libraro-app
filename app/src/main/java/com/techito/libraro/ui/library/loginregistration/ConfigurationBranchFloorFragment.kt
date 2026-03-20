package com.techito.libraro.ui.library.loginregistration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.techito.libraro.R
import com.techito.libraro.databinding.FragmentConfigurationBranchFloorBinding
import com.techito.libraro.model.BranchConfigurationPlan
import com.techito.libraro.model.LibraryDetail
import com.techito.libraro.model.StaticMonthlyOption
import com.techito.libraro.ui.library.adapter.BranchConfigurationFloorAdapter
import com.techito.libraro.utils.AppUtils
import com.techito.libraro.viewmodel.BranchConfigurationViewModel

class ConfigurationBranchFloorFragment : Fragment() {

    private lateinit var binding: FragmentConfigurationBranchFloorBinding
    private val viewModel: BranchConfigurationViewModel by activityViewModels()
    private lateinit var floorAdapter: BranchConfigurationFloorAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_configuration_branch_floor,
            container,
            false
        )
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupFloorList()
        setupNavigationObserver()
        setupDatePicker()
        setupObservers()

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.fetchMasterStaticData()
        }

        binding.mcvAddFloor.setOnClickListener {
            viewModel.addFloor()
        }
        binding.btnSaveNext.setOnClickListener {
            viewModel.onSaveAndNextClicked()
        }
        if (viewModel.staticData.value == null || viewModel.staticData.value?.data == null) {
            viewModel.fetchMasterStaticData()
        }
        if (viewModel.libraryDetails.value == null) {
            viewModel.fetchLibraryDetails()
        }
    }

    private fun setupObservers() {
        viewModel.staticData.observe(viewLifecycleOwner) { response ->
            binding.swipeRefresh.isRefreshing = false
            response?.data?.let { data ->
                val monthlyOptions = data.monthlyOptions?.filterNotNull() ?: emptyList()
                viewModel.monthlyOptions.value = monthlyOptions as MutableList<StaticMonthlyOption>

                val planDaysLabels =
                    monthlyOptions.map { it.label ?: "" }.filter { it.isNotEmpty() }
                viewModel.planDaysLabels.value = planDaysLabels
                val planDaysAdapter =
                    ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_dropdown_item_1line,
                        planDaysLabels
                    )
                binding.actvPlanDays.setAdapter(planDaysAdapter)

                if(viewModel.planDays.value == null){
                    val selectedPlanData =
                        viewModel.monthlyOptions.value?.find {
                            it.label == planDaysLabels.get(0)
                        }
                    selectedPlanData?.let {
                        viewModel.planDays.value = BranchConfigurationPlan(it.value, "1 MONTH")
                    }
                }

                val operatingHrsList = (10..24).map { it.toString() }
                val opAdapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    operatingHrsList
                )
                binding.actvOperatingHrs.setAdapter(opAdapter)

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

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.layoutProgress.clProgress.isVisible = isLoading
            if (!isLoading) {
                binding.swipeRefresh.isRefreshing = false
            }
        }
    }

    private fun setupDatePicker() {
        binding.etFoundersDay.setOnClickListener {
            AppUtils.showDatePicker(
                parentFragmentManager,
                "Select Founder's Day"
            ) { selectedDate ->
                viewModel.founderDay.value = selectedDate
            }
        }
    }

    private fun setupNavigationObserver() {
        viewModel.navigateToAddShifts.observe(viewLifecycleOwner) { shouldNavigate ->
            if (shouldNavigate) {
                findNavController().navigate(R.id.action_configurationBranchFloorFragment_to_configurationBranchShiftFragment)
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

        viewModel.floors.observe(viewLifecycleOwner) { floors ->
            floorAdapter.updateData(floors)
        }
    }
}
