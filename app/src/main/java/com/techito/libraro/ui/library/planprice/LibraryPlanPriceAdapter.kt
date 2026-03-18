package com.techito.libraro.ui.library.planprice

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.techito.libraro.databinding.ItemLibraryPlanPriceBinding

class LibraryPlanPriceAdapter(
    private val onEditClick: (LibraryPlanPriceActivity.DummyPlanPrice, Int) -> Unit,
    private val onDeleteClick: (LibraryPlanPriceActivity.DummyPlanPrice, Int) -> Unit
) : ListAdapter<LibraryPlanPriceActivity.DummyPlanPrice, LibraryPlanPriceAdapter.PlanPriceViewHolder>(PlanPriceDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanPriceViewHolder {
        val binding = ItemLibraryPlanPriceBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PlanPriceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlanPriceViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    inner class PlanPriceViewHolder(private val binding: ItemLibraryPlanPriceBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(planPrice: LibraryPlanPriceActivity.DummyPlanPrice, position: Int) {
            binding.apply {
                tvPlanName.text = planPrice.planName
                tvPlanType.text = planPrice.planType
                tvPlanPrice.text = planPrice.price

                ivEdit.setOnClickListener { onEditClick(planPrice, position) }
                ivDelete.setOnClickListener { onDeleteClick(planPrice, position) }
            }
        }
    }

    class PlanPriceDiffCallback : DiffUtil.ItemCallback<LibraryPlanPriceActivity.DummyPlanPrice>() {
        override fun areItemsTheSame(oldItem: LibraryPlanPriceActivity.DummyPlanPrice, newItem: LibraryPlanPriceActivity.DummyPlanPrice): Boolean {
            return oldItem.planName == newItem.planName && oldItem.planType == newItem.planType
        }

        override fun areContentsTheSame(oldItem: LibraryPlanPriceActivity.DummyPlanPrice, newItem: LibraryPlanPriceActivity.DummyPlanPrice): Boolean {
            return oldItem == newItem
        }
    }
}