package com.techito.libraro.ui.library.plantypeorshift

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.techito.libraro.databinding.ItemLibraryPlanTypeShiftBinding

class LibraryPlanTypeShiftAdapter(
    private val onEditClick: (LibraryPlanTypeShiftActivity.DummyShift, Int) -> Unit,
    private val onDeleteClick: (LibraryPlanTypeShiftActivity.DummyShift, Int) -> Unit
) : ListAdapter<LibraryPlanTypeShiftActivity.DummyShift, LibraryPlanTypeShiftAdapter.ShiftViewHolder>(ShiftDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShiftViewHolder {
        val binding = ItemLibraryPlanTypeShiftBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ShiftViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ShiftViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    inner class ShiftViewHolder(private val binding: ItemLibraryPlanTypeShiftBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(shift: LibraryPlanTypeShiftActivity.DummyShift, position: Int) {
            binding.apply {
                tvShiftName.text = shift.name
                tvStartTime.text = shift.startTime
                tvEndTime.text = shift.endTime
                tvDuration.text = shift.duration

                ivEdit.setOnClickListener { onEditClick(shift, position) }
                ivDelete.setOnClickListener { onDeleteClick(shift, position) }
            }
        }
    }

    class ShiftDiffCallback : DiffUtil.ItemCallback<LibraryPlanTypeShiftActivity.DummyShift>() {
        override fun areItemsTheSame(oldItem: LibraryPlanTypeShiftActivity.DummyShift, newItem: LibraryPlanTypeShiftActivity.DummyShift): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: LibraryPlanTypeShiftActivity.DummyShift, newItem: LibraryPlanTypeShiftActivity.DummyShift): Boolean {
            return oldItem == newItem
        }
    }
}