package com.techito.libraro.ui.library.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.techito.libraro.databinding.ItemFloorBinding
import com.techito.libraro.model.BranchConfigurationFloor

class BranchConfigurationFloorAdapter(
    private var floors: List<BranchConfigurationFloor>,
    private val onDeleteClick: (Int) -> Unit
) : RecyclerView.Adapter<BranchConfigurationFloorAdapter.FloorViewHolder>() {

    fun updateData(newFloors: List<BranchConfigurationFloor>) {
        floors = newFloors
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FloorViewHolder {
        val binding = ItemFloorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FloorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FloorViewHolder, position: Int) {
        holder.bind(floors[position], position, floors.size)
    }

    override fun getItemCount(): Int = floors.size

    inner class FloorViewHolder(private val binding: ItemFloorBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(floor: BranchConfigurationFloor, position: Int, totalSize: Int) {
            binding.floor = floor
            binding.position = position
            
            // Sync UI changes back to the model immediately
            // Using manual listeners because seatFrom and seatTo are Int?
            binding.etFloorName.doAfterTextChanged { floor.floorName = it.toString() }
            binding.etSeatFrom.doAfterTextChanged { 
                floor.seatFrom = it.toString().toIntOrNull() 
            }
            binding.etSeatTo.doAfterTextChanged { 
                floor.seatTo = it.toString().toIntOrNull() 
            }

            if (totalSize > 1) {
                binding.ivDeleteFloor.visibility = View.VISIBLE
                binding.ivDeleteFloor.setOnClickListener { onDeleteClick(position) }
            } else {
                binding.ivDeleteFloor.visibility = View.GONE
            }

            binding.executePendingBindings()
        }
    }
}
