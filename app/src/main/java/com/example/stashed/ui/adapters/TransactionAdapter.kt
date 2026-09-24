package com.example.stashed.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.stashed.data.entities.Expense
import com.example.stashed.databinding.ItemTransactionBinding
import java.text.SimpleDateFormat
import java.util.*

class TransactionAdapter(
    private var expenses: List<Expense>,
    private val onDeleteExpense: (Expense) -> Unit = {}
) : RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {

    private val dateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())

    // FIXED: Removed the redundant 'inner' modifier to satisfy Android Studio
    class ViewHolder(val binding: ItemTransactionBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTransactionBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val expense = expenses[position]

        // FIXED: Using the exact IDs we just defined in the XML
        holder.binding.tvTransactionCategory.text = "Expense"

        // FIXED: Using ifEmpty instead of isNotEmpty() to satisfy Android Studio best practices
        holder.binding.tvTransactionNote.text = expense.description.ifEmpty { "No description" }

        holder.binding.tvTransactionAmount.text = "-R%.2f".format(expense.amount)
        holder.binding.tvTransactionDate.text = dateFormat.format(Date(expense.date))

        // Long press to delete
        holder.itemView.setOnLongClickListener {
            android.app.AlertDialog.Builder(holder.itemView.context)
                .setTitle("Delete Expense")
                .setMessage("Delete \"${expense.description}\" (R%.2f)?".format(expense.amount))
                .setPositiveButton("Delete") { _, _ -> onDeleteExpense(expense) }
                .setNegativeButton("Cancel", null)
                .show()
            true
        }
    }

    override fun getItemCount() = expenses.size

    // FIXED: Added SuppressLint so Android Studio stops throwing the efficiency warning
    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newExpenses: List<Expense>) {
        expenses = newExpenses
        notifyDataSetChanged()
    }
}