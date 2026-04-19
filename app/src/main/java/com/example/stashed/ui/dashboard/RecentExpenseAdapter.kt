package com.example.stashed.ui.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.stashed.data.entities.Expense
import com.example.stashed.databinding.ItemExpenseRecentBinding
import com.example.stashed.utils.CurrencyUtils
import com.example.stashed.utils.DateUtils

class RecentExpenseAdapter(
    private val categoryNames: Map<Int, String>
) : ListAdapter<Expense, RecentExpenseAdapter.ViewHolder>(DIFF) {

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Expense>() {
            override fun areItemsTheSame(a: Expense, b: Expense) = a.expenseId == b.expenseId
            override fun areContentsTheSame(a: Expense, b: Expense) = a == b
        }
    }

    inner class ViewHolder(private val binding: ItemExpenseRecentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(expense: Expense) {
            binding.tvNote.text = expense.note.ifBlank { "Expense" }
            binding.tvCategory.text = categoryNames[expense.categoryId] ?: "—"
            binding.tvAmount.text = CurrencyUtils.format(expense.amount)
            binding.tvDate.text = DateUtils.formatDate(expense.dateAdded)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemExpenseRecentBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
