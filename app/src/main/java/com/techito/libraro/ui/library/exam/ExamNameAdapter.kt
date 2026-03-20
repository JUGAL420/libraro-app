package com.techito.libraro.ui.library.exam

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.techito.libraro.databinding.ItemExamNameBinding
import com.techito.libraro.model.ExamName

class ExamNameAdapter(
    private val onEditClick: (ExamName) -> Unit,
    private val onDeleteClick: (ExamName) -> Unit
) : ListAdapter<ExamName, ExamNameAdapter.ExamViewHolder>(ExamDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExamViewHolder {
        val binding = ItemExamNameBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ExamViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExamViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ExamViewHolder(private val binding: ItemExamNameBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(exam: ExamName) {
            binding.apply {
                tvExamName.text = exam.name
                tvStatus.text = if (exam.isActive) "● Active" else "● Inactive"
                
                val color = if (exam.isActive) 
                    ContextCompat.getColor(root.context, android.R.color.holo_green_dark) 
                else 
                    ContextCompat.getColor(root.context, android.R.color.holo_red_dark)
                
                tvStatus.setTextColor(color)

                ivEdit.setOnClickListener { onEditClick(exam) }
                ivDelete.setOnClickListener { onDeleteClick(exam) }
            }
        }
    }

    class ExamDiffCallback : DiffUtil.ItemCallback<ExamName>() {
        override fun areItemsTheSame(oldItem: ExamName, newItem: ExamName): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ExamName, newItem: ExamName): Boolean {
            return oldItem == newItem
        }
    }
}
