package com.techito.libraro.ui.library.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.techito.libraro.databinding.ItemShiftBinding
import com.techito.libraro.model.BranchConfigurationShift

class BranchConfigurationShiftAdapter(
    private var shifts: List<BranchConfigurationShift>,
    private val onShiftName: (Int, AutoCompleteTextView) -> Unit,
    private val onDeleteClick: (Int) -> Unit,
    private val onTimeClick: (Int, Boolean) -> Unit
) : RecyclerView.Adapter<BranchConfigurationShiftAdapter.ShiftViewHolder>() {

    fun updateData(newShifts: List<BranchConfigurationShift>) {
        shifts = newShifts
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShiftViewHolder {
        val binding = ItemShiftBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ShiftViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ShiftViewHolder, position: Int) {
        holder.bind(shifts[position], position, shifts.size)
    }

    override fun getItemCount(): Int = shifts.size

    inner class ShiftViewHolder(private val binding: ItemShiftBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(shift: BranchConfigurationShift, position: Int, totalSize: Int) {
            binding.shift = shift
            binding.position = position
            
            // Sync UI changes back to the shared ViewModel list
            binding.etShiftName.doAfterTextChanged { shift.type = it.toString() }
            binding.etCustomName.doAfterTextChanged { shift.customName = it.toString() }
            binding.etDuration.doAfterTextChanged { shift.durationHours = it.toString() }
            binding.etPrice.doAfterTextChanged { shift.price = it.toString() }

            if (totalSize > 1) {
                binding.ivDeleteShift.visibility = View.VISIBLE
                binding.ivDeleteShift.setOnClickListener { onDeleteClick(position) }
            } else {
                binding.ivDeleteShift.visibility = View.GONE
            }

            binding.etShiftName.setOnClickListener { onShiftName(position, binding.etShiftName) }
            binding.tilShiftName.setOnClickListener { binding.etShiftName.performClick() }

            binding.etStartTime.setOnClickListener { onTimeClick(position, true) }
            binding.etEndTime.setOnClickListener { onTimeClick(position, false) }

            binding.executePendingBindings()
        }
    }
}
