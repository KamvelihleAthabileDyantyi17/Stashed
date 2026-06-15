package com.example.stashed.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.stashed.R
import com.example.stashed.data.entities.Expense
import java.text.SimpleDateFormat
import java.util.*

class TransactionAdapter(
    private var expenses: List<Expense>,
    private val onDeleteExpense: (Expense) -> Unit = {}
) : RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {

    private val dateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvCategory: TextView = view.findViewById(R.id.tvTransactionCategory)
        val tvNote: TextView     = view.findViewById(R.id.tvTransactionNote)
        val tvAmount: TextView   = view.findViewById(R.id.tvTransactionAmount)
        val tvDate: TextView     = view.findViewById(R.id.tvTransactionDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val expense = expenses[position]
        holder.tvCategory.text = "Expense"
        holder.tvNote.text     = if (expense.description.isNotEmpty()) expense.description else "No description"
        holder.tvAmount.text   = "-R%.2f".format(expense.amount)
        holder.tvDate.text     = dateFormat.format(Date(expense.date))

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

    fun updateData(newExpenses: List<Expense>) {
        expenses = newExpenses
        notifyDataSetChanged()
    }
}