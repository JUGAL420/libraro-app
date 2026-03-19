package com.techito.libraro.ui.library.branch.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.techito.libraro.databinding.ItemLibraryFeatureBinding

class LibraryFeatureAdapter(private val features: List<Feature>) :
    RecyclerView.Adapter<LibraryFeatureAdapter.ViewHolder>() {

    private var selectedPosition = 0

    data class Feature(val name: String, val icon: Int)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLibraryFeatureBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(features[position], position == selectedPosition)
        holder.itemView.setOnClickListener {
            val previousSelected = selectedPosition
            selectedPosition = holder.adapterPosition
            notifyItemChanged(previousSelected)
            notifyItemChanged(selectedPosition)
        }
    }

    override fun getItemCount(): Int = features.size

    inner class ViewHolder(private val binding: ItemLibraryFeatureBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(feature: Feature, isSelected: Boolean) {
            binding.featureName = feature.name
            binding.isSelected = isSelected
            binding.ivFeatureIcon.setImageResource(feature.icon)
            binding.executePendingBindings()
        }
    }
}
