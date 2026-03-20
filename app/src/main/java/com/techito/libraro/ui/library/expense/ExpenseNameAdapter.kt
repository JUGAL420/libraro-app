package com.techito.libraro.ui.library.expense

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.techito.libraro.databinding.ItemExpenseNameBinding
import com.techito.libraro.model.ExpenseName

class ExpenseNameAdapter(
    private val onEditClick: (ExpenseName) -> Unit,
    private val onDeleteClick: (ExpenseName) -> Unit
) : ListAdapter<ExpenseName, ExpenseNameAdapter.ExpenseViewHolder>(ExpenseDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val binding = ItemExpenseNameBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ExpenseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ExpenseViewHolder(private val binding: ItemExpenseNameBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(expense: ExpenseName) {
            binding.apply {
                tvExpenseName.text = expense.name
                tvStatus.text = if (expense.isActive) "● Active" else "● Inactive"
                
                val color = if (expense.isActive) 
                    ContextCompat.getColor(root.context, android.R.color.holo_green_dark) 
                else 
                    ContextCompat.getColor(root.context, android.R.color.holo_red_dark)
                
                tvStatus.setTextColor(color)

                ivEdit.setOnClickListener { onEditClick(expense) }
                ivDelete.setOnClickListener { onDeleteClick(expense) }
            }
        }
    }

    class ExpenseDiffCallback : DiffUtil.ItemCallback<ExpenseName>() {
        override fun areItemsTheSame(oldItem: ExpenseName, newItem: ExpenseName): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ExpenseName, newItem: ExpenseName): Boolean {
            return oldItem == newItem
        }
    }
}
