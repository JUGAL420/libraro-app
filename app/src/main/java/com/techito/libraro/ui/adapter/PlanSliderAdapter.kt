package com.techito.libraro.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.techito.libraro.databinding.ItemPlanCardBinding
import com.techito.libraro.model.Plan

class PlanSliderAdapter : RecyclerView.Adapter<PlanSliderAdapter.PlanViewHolder>() {

    private var plans: List<Plan> = emptyList()

    fun setPlans(newPlans: List<Plan>) {
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

        fun bind(plan: Plan) {
            binding.plan = plan
            binding.rvBenefits.layoutManager = LinearLayoutManager(binding.root.context)
            binding.rvBenefits.adapter = BenefitAdapter(plan.benefits)
            binding.executePendingBindings()
        }
    }
}
