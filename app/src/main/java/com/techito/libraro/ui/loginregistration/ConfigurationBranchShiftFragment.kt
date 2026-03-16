package com.techito.libraro.ui.loginregistration

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
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.techito.libraro.R
import com.techito.libraro.databinding.FragmentConfigurationBranchShiftBinding
import com.techito.libraro.ui.MainActivity
import com.techito.libraro.ui.adapter.BranchConfigurationShiftAdapter
import com.techito.libraro.viewmodel.BranchConfigurationViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

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
//                AppUtils.showToast(requireContext(), "Branch Configuration Saved Successfully!")
                // Navigate to next screen (e.g., Dashboard)
                startActivity(Intent(requireActivity(), MainActivity::class.java))
                requireActivity().finish()
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
        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(12)
            .setMinute(0)
            .setTitleText("Select Time")
            .build()

        picker.addOnPositiveButtonClickListener {
            val hour24 = picker.hour
            val minute = picker.minute
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, hour24)
            calendar.set(Calendar.MINUTE, minute)

            val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
            val formattedTime = sdf.format(calendar.time)

            val shifts = viewModel.shifts.value ?: return@addOnPositiveButtonClickListener
            val currentShift = shifts.get(position)
            if (isStartTime) {
                currentShift.startTime = formattedTime
            } else {
                currentShift.endTime = formattedTime
            }
            shifts[position] = currentShift
            viewModel.shifts.value = shifts // Trigger update
            viewModel.calculateDuration(position)
        }

        picker.show(childFragmentManager, "TIME_PICKER")
    }
}
