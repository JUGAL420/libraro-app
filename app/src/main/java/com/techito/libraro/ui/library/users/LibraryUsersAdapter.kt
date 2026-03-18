package com.techito.libraro.ui.library.users

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.techito.libraro.databinding.ItemLibraryUserBinding

class LibraryUsersAdapter(
    private val onEditClick: (Int) -> Unit,
    private val onDeleteClick: (Int) -> Unit,
    private val onPermissionClick: (Int) -> Unit,
) : RecyclerView.Adapter<LibraryUsersAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemLibraryUserBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = 4

    inner class UserViewHolder(private val binding: ItemLibraryUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position:Int) {
            binding.ivEdit.setOnClickListener {
                onEditClick(position)
            }
            binding.ivDelete.setOnClickListener {
                onDeleteClick(position)
            }
            binding.ivPermission.setOnClickListener {
                onPermissionClick(position)
            }
            binding.executePendingBindings()
        }
    }
}