package com.example.stashed.ui.budget

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.stashed.data.entities.Category
import com.example.stashed.databinding.ItemCategoryBudgetBinding
import com.example.stashed.utils.CurrencyUtils

class BudgetCategoryAdapter(
    private val onEditLimit: (Category) -> Unit,
    private val onDelete: (Category) -> Unit
) : ListAdapter<Category, BudgetCategoryAdapter.ViewHolder>(DIFF) {

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Category>() {
            override fun areItemsTheSame(a: Category, b: Category) = a.categoryId == b.categoryId
            override fun areContentsTheSame(a: Category, b: Category) = a == b
        }
    }

    inner class ViewHolder(private val binding: ItemCategoryBudgetBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(cat: Category) {
            binding.tvCategoryName.text = cat.name
            binding.tvSpent.text = if (cat.isDefault) "DEFAULT" else "CUSTOM"
            binding.tvLimit.text = if (cat.budgetLimit > 0)
                CurrencyUtils.format(cat.budgetLimit) else "No limit set"
            binding.progressBar.progress = 0
            binding.tvPercent.text = ""
            binding.root.setOnClickListener { onEditLimit(cat) }
            binding.root.setOnLongClickListener { onDelete(cat); true }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCategoryBudgetBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
