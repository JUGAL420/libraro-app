package com.techito.libraro.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.techito.libraro.databinding.ItemPlanBenefitBinding

class BenefitAdapter(private val benefits: List<String>) :
    RecyclerView.Adapter<BenefitAdapter.BenefitViewHolder>() {

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
        fun bind(benefit: String) {
            binding.benefit = benefit
            binding.executePendingBindings()
        }
    }
}
