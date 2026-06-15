package com.example.stashed.ui.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.stashed.R
import com.example.stashed.databinding.ItemCategoryBudgetBinding
import com.example.stashed.utils.CurrencyUtils

class CategoryBudgetAdapter : ListAdapter<CategoryBudgetItem, CategoryBudgetAdapter.ViewHolder>(DIFF) {

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<CategoryBudgetItem>() {
            override fun areItemsTheSame(a: CategoryBudgetItem, b: CategoryBudgetItem) =
                a.category.categoryId == b.category.categoryId
            override fun areContentsTheSame(a: CategoryBudgetItem, b: CategoryBudgetItem) = a == b
        }
    }

    inner class ViewHolder(private val binding: ItemCategoryBudgetBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CategoryBudgetItem) {
            binding.tvCategoryName.text = item.category.name
            binding.tvSpent.text = CurrencyUtils.format(item.spent)
            binding.tvLimit.text = if (item.category.budgetLimit > 0)
                "/ ${CurrencyUtils.format(item.category.budgetLimit)}"
            else "/ No limit"

            val progress = item.percentage.coerceIn(0.0, 100.0).toInt()
            binding.progressBar.progress = progress

            val colorRes = when (item.status) {
                BudgetStatus.GOOD    -> R.color.semanticSuccess
                BudgetStatus.WARNING -> R.color.semanticWarning
                BudgetStatus.DANGER  -> R.color.semanticDanger
            }
            binding.progressBar.progressTintList =
                ContextCompat.getColorStateList(binding.root.context, colorRes)

            binding.tvPercent.text = "${progress}%"
            binding.tvPercent.setTextColor(
                ContextCompat.getColor(binding.root.context, colorRes)
            )
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
