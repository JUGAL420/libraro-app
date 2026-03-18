package com.techito.libraro.ui.library.branchfloor

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.techito.libraro.databinding.ItemBranchFloorBinding
import com.techito.libraro.model.BranchConfigurationFloor

class BranchFloorAdapter(
    private val onEditClick: (BranchConfigurationFloor, Int) -> Unit,
    private val onDeleteClick: (BranchConfigurationFloor, Int) -> Unit
) : ListAdapter<BranchConfigurationFloor, BranchFloorAdapter.FloorViewHolder>(FloorDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FloorViewHolder {
        val binding = ItemBranchFloorBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FloorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FloorViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    inner class FloorViewHolder(private val binding: ItemBranchFloorBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(floor: BranchConfigurationFloor, position: Int) {
            binding.apply {
                tvFloorName.text = floor.floorName ?: ""
                tvFromSeat.text = (floor.seatFrom ?: "").toString()
                tvToSeat.text = (floor.seatTo ?: "").toString()

                val totalSeats = if (floor.seatTo != null && floor.seatFrom != null) {
                    floor.seatTo!! - floor.seatFrom!! + 1
                } else 0
                tvTotalSeats.text = totalSeats.toString()

                ivEdit.setOnClickListener { onEditClick(floor, position) }
                ivDelete.setOnClickListener { onDeleteClick(floor, position) }
            }
        }
    }

    class FloorDiffCallback : DiffUtil.ItemCallback<BranchConfigurationFloor>() {
        override fun areItemsTheSame(oldItem: BranchConfigurationFloor, newItem: BranchConfigurationFloor): Boolean {
            return oldItem.floorName == newItem.floorName
        }

        override fun areContentsTheSame(oldItem: BranchConfigurationFloor, newItem: BranchConfigurationFloor): Boolean {
            return oldItem == newItem
        }
    }
}