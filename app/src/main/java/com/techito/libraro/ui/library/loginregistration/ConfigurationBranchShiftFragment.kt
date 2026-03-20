package com.techito.libraro.ui.library.loginregistration

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.techito.libraro.LibraroApp
import com.techito.libraro.R
import com.techito.libraro.databinding.FragmentConfigurationBranchShiftBinding
import com.techito.libraro.ui.LoginOptionActivity
import com.techito.libraro.ui.library.MainActivity
import com.techito.libraro.ui.library.adapter.BranchConfigurationShiftAdapter
import com.techito.libraro.utils.AppUtils
import com.techito.libraro.viewmodel.BranchConfigurationViewModel
import kotlinx.coroutines.launch

class ConfigurationBranchShiftFragment : Fragment() {

    private lateinit var binding: FragmentConfigurationBranchShiftBinding
    private val viewModel: BranchConfigurationViewModel by activityViewModels()
    private lateinit var shiftAdapter: BranchConfigurationShiftAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_configuration_branch_shift,
            container,
            false
        )
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupShiftList()
        setupObservers()
        binding.tvShiftDuration.text =
            getString(R.string.library_operating_hrs_duration_hrs).replace(
                "{duration}",
                viewModel.operatingHrs.value ?: ""
            )
        binding.mcvAddShift.setOnClickListener {
            viewModel.addShift()
        }

    }

    private fun setupObservers() {
        // Observe Loading state
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.layoutProgress.clProgress.isVisible = isLoading
        }

        // Observe Success state
        viewModel.configurationSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                lifecycleScope.launch {
                    LibraroApp.preferenceManager.setLoggedIn(true)
                    startActivity(Intent(requireActivity(), MainActivity::class.java))
                    requireActivity().finishAffinity()
                }
            }
        }

    }

    private fun setupShiftList() {
        shiftAdapter = BranchConfigurationShiftAdapter(
            viewModel.shifts.value ?: emptyList(),
            onShiftName = { position, dropDownView -> addShiftName(position, dropDownView) },
            onDeleteClick = { position -> viewModel.removeShift(position) },
            onTimeClick = { position, isStartTime ->
                showTimePicker(position, isStartTime)
            }
        )
        binding.rvShifts.adapter = shiftAdapter

        viewModel.shifts.observe(viewLifecycleOwner) { shifts ->
            shiftAdapter.updateData(shifts)
        }
    }

    private fun addShiftName(position: Int, dropDownView: AutoCompleteTextView) {
        val names = viewModel.planTypeNames.value ?: return
        // Set adapter if not already set or data changed
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, names)
        dropDownView.setAdapter(adapter)

        dropDownView.setOnItemClickListener { _, _, index, _ ->
            val selectedType = names[index]
            viewModel.updateShiftType(position, selectedType)
        }
    }

    private fun showTimePicker(position: Int, isStartTime: Boolean) {
        AppUtils.showTimePicker(
            childFragmentManager,
            if (isStartTime) "Select Start Time" else "Select End Time"
        ) { formattedTime ->
            val shifts = viewModel.shifts.value ?: return@showTimePicker
            val currentShift = shifts[position]
            if (isStartTime) {
                currentShift.startTime = formattedTime
            } else {
                currentShift.endTime = formattedTime
            }
            shifts[position] = currentShift
            viewModel.shifts.value = shifts // Trigger update
            viewModel.calculateDuration(position)
        }
    }
}
