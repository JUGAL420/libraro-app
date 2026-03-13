package com.techito.libraro.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.techito.libraro.databinding.ItemShiftBinding
import com.techito.libraro.model.Shift

class ShiftAdapter(
    private var shifts: List<Shift>,
    private val onAddClick: () -> Unit,
    private val onDeleteClick: (Int) -> Unit,
    private val onTimeClick: (Int, Boolean) -> Unit
) : RecyclerView.Adapter<ShiftAdapter.ShiftViewHolder>() {

    fun updateData(newShifts: List<Shift>) {
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

        fun bind(shift: Shift, position: Int, totalSize: Int) {
            binding.shift = shift
            binding.position = position

            if (totalSize > 1) {
                binding.ivDeleteShift.visibility = View.VISIBLE
                binding.ivDeleteShift.setOnClickListener { onDeleteClick(position) }
            } else {
                binding.ivDeleteShift.visibility = View.GONE
            }

            binding.tilStartTime.setOnClickListener { onTimeClick(position, true) }
            binding.tilEndTime.setOnClickListener { onTimeClick(position, false) }

            binding.executePendingBindings()
        }
    }
}
