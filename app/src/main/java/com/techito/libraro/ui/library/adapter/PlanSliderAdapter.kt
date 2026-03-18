package com.techito.libraro.ui.library.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.techito.libraro.databinding.ItemPlanCardBinding
import com.techito.libraro.model.PlanData

class PlanSliderAdapter : RecyclerView.Adapter<PlanSliderAdapter.PlanViewHolder>() {

    private var plans: List<PlanData> = emptyList()

    fun setPlans(newPlans: List<PlanData>) {
        plans = newPlans
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanViewHolder {
        val binding = ItemPlanCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PlanViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlanViewHolder, position: Int) {
        holder.bind(plans[position])
    }

    override fun getItemCount(): Int = plans.size

    class PlanViewHolder(private val binding: ItemPlanCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(plan: PlanData) {
            binding.planData = plan
            binding.tvOriginalPrice.paintFlags =
                binding.tvOriginalPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            val originalPrice = plan.originalPrice?.toDoubleOrNull() ?: 0.0
            binding.tvOriginalPrice.isVisible = originalPrice > 0.0

            binding.rvBenefits.layoutManager = LinearLayoutManager(binding.root.context)
            // Use features from PlanData
            val benefits = plan.features?.filterNotNull() ?: emptyList()
            binding.rvBenefits.adapter = PlanBenefitAdapter(benefits)
            binding.executePendingBindings()
        }
    }
}
