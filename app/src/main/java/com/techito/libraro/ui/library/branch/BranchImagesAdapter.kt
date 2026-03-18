package com.techito.libraro.ui.library.branch

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.techito.libraro.databinding.ItemBranchImageBinding

class BranchImagesAdapter : RecyclerView.Adapter<BranchImagesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemBranchImageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Static size 4, no data binding for now as per "static size 4" request
    }

    override fun getItemCount(): Int = 4

    inner class ViewHolder(private val binding: ItemBranchImageBinding) :
        RecyclerView.ViewHolder(binding.root)
}
