package com.techito.libraro.ui.library.branch.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.techito.libraro.databinding.ItemLibraryEditableImageBinding

class LibraryImageAdapter(
    private val onAddClick: () -> Unit,
    private val onRemoveClick: (Int) -> Unit
) : RecyclerView.Adapter<LibraryImageAdapter.ViewHolder>() {

    private val images = mutableListOf<Uri>()

    fun setImages(newImages: List<Uri>) {
        images.clear()
        images.addAll(newImages)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLibraryEditableImageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position == 0) {
            holder.bindAdd()
        } else {
            holder.bindImage(images[position - 1], position - 1)
        }
    }

    override fun getItemCount(): Int {
        // First position is always "Add Image"
        // Max 4 images + 1 add button = 5 items max
        return (images.size + 1).coerceAtMost(5)
    }

    inner class ViewHolder(private val binding: ItemLibraryEditableImageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindAdd() {
            binding.ivImage.visibility = View.GONE
            binding.ivRemove.visibility = View.GONE
            binding.ivAddImage.visibility = View.VISIBLE
            binding.root.setOnClickListener { onAddClick() }
        }

        fun bindImage(uri: Uri, index: Int) {
            binding.ivImage.visibility = View.VISIBLE
            binding.ivRemove.visibility = View.VISIBLE
            binding.ivAddImage.visibility = View.GONE
            binding.ivImage.setImageURI(uri)
            binding.ivRemove.setOnClickListener { onRemoveClick(index) }
            binding.root.setOnClickListener(null)
        }
    }
}
