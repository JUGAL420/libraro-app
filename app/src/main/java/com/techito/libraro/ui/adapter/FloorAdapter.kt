package com.techito.libraro.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.techito.libraro.databinding.ItemFloorBinding
import com.techito.libraro.model.Floor

class FloorAdapter(
    private var floors: List<Floor>,
    private val onDeleteClick: (Int) -> Unit
) : RecyclerView.Adapter<FloorAdapter.FloorViewHolder>() {

    fun updateData(newFloors: List<Floor>) {
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

        fun bind(floor: Floor, position: Int, totalSize: Int) {
            binding.floor = floor
            binding.position = position

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
