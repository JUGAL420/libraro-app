package com.techito.libraro.ui.library.plan

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.techito.libraro.databinding.ItemLibraryPlanBinding

class LibraryPlanAdapter(
    private val onEditClick: (LibraryPlanActivity.DummyPlan, Int) -> Unit,
    private val onDeleteClick: (LibraryPlanActivity.DummyPlan, Int) -> Unit
) : ListAdapter<LibraryPlanActivity.DummyPlan, LibraryPlanAdapter.PlanViewHolder>(PlanDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanViewHolder {
        val binding = ItemLibraryPlanBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PlanViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlanViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    inner class PlanViewHolder(private val binding: ItemLibraryPlanBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(plan: LibraryPlanActivity.DummyPlan, position: Int) {
            binding.apply {
                tvPlanName.text = plan.name
                tvMonths.text = plan.months
                tvDays.text = plan.days

                ivEdit.setOnClickListener { onEditClick(plan, position) }
                ivDelete.setOnClickListener { onDeleteClick(plan, position) }
            }
        }
    }

    class PlanDiffCallback : DiffUtil.ItemCallback<LibraryPlanActivity.DummyPlan>() {
        override fun areItemsTheSame(oldItem: LibraryPlanActivity.DummyPlan, newItem: LibraryPlanActivity.DummyPlan): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: LibraryPlanActivity.DummyPlan, newItem: LibraryPlanActivity.DummyPlan): Boolean {
            return oldItem == newItem
        }
    }
}