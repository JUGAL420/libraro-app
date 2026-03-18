package com.techito.libraro.ui.library.branch

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.techito.libraro.databinding.ItemLibraryBranchBinding

class LibraryBranchAdapter(
    private val onEditClick: (LibraryBranchMasterActivity.DummyBranch, Int) -> Unit,
    private val onBookingClick: (LibraryBranchMasterActivity.DummyBranch, Int) -> Unit
) : ListAdapter<LibraryBranchMasterActivity.DummyBranch, LibraryBranchAdapter.BranchViewHolder>(BranchDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BranchViewHolder {
        val binding = ItemLibraryBranchBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BranchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BranchViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    inner class BranchViewHolder(private val binding: ItemLibraryBranchBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(branch: LibraryBranchMasterActivity.DummyBranch, position: Int) {
            binding.apply {
                tvBranchName.text = branch.name
                tvMobile.text = branch.mobile
                tvEmail.text = branch.email
                tvAddress.text = "Address: ${branch.address}"
                
                rvBranchImage.adapter = BranchImagesAdapter()
                
                ivEdit.setOnClickListener { onEditClick(branch, position) }
                ivBooking.setOnClickListener { onBookingClick(branch, position) }
            }
        }
    }

    class BranchDiffCallback : DiffUtil.ItemCallback<LibraryBranchMasterActivity.DummyBranch>() {
        override fun areItemsTheSame(oldItem: LibraryBranchMasterActivity.DummyBranch, newItem: LibraryBranchMasterActivity.DummyBranch): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: LibraryBranchMasterActivity.DummyBranch, newItem: LibraryBranchMasterActivity.DummyBranch): Boolean {
            return oldItem == newItem
        }
    }
}
