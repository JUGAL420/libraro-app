package com.techito.libraro.ui.library.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.techito.libraro.databinding.ItemIntroSliderBinding
import com.techito.libraro.model.IntroSlide

class IntroSliderAdapter(private val introSlides: List<IntroSlide>) :
    RecyclerView.Adapter<IntroSliderAdapter.IntroSlideViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IntroSlideViewHolder {
        return IntroSlideViewHolder(
            ItemIntroSliderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: IntroSlideViewHolder, position: Int) {
        holder.bind(introSlides[position])
    }

    override fun getItemCount(): Int {
        return introSlides.size
    }

    inner class IntroSlideViewHolder(private val binding: ItemIntroSliderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(introSlide: IntroSlide) {
            binding.tvTitle.text = introSlide.title
            binding.tvDescription.text = introSlide.description
            binding.ivIntroImage.setImageResource(introSlide.image)
        }
    }
}
