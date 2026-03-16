package com.techito.libraro.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.techito.libraro.R
import com.techito.libraro.databinding.ItemPlanBenefitBinding
import com.techito.libraro.model.PlanFeature

class PlanBenefitAdapter(private val benefits: List<PlanFeature>) :
    RecyclerView.Adapter<PlanBenefitAdapter.BenefitViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BenefitViewHolder {
        val binding = ItemPlanBenefitBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BenefitViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BenefitViewHolder, position: Int) {
        holder.bind(benefits[position])
    }

    override fun getItemCount(): Int = benefits.size

    class BenefitViewHolder(private val binding: ItemPlanBenefitBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(benefit: PlanFeature) {
            binding.benefit = benefit.name
            binding.statusImage.setImageResource(if (benefit.enabled == true) R.drawable.ic_check_green else R.drawable.ic_cross_red)
            binding.executePendingBindings()
        }
    }
}
