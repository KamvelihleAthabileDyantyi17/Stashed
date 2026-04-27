package com.example.stashed.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.lifecycleScope
import com.example.stashed.R
import com.example.stashed.data.AppDatabase
import com.example.stashed.data.entities.Expense
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class AddExpenseDialog(
    private val userId: Int,
    private val onExpenseAdded: () -> Unit
) : BottomSheetDialogFragment() {

    private val categories = listOf(
        "Food", "Transport", "Entertainment", "Shopping", "Bills", "Health", "Other"
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.dialog_add_expenses, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etAmount      = view.findViewById<EditText>(R.id.etExpenseAmount)
        val etDescription = view.findViewById<EditText>(R.id.etExpenseDescription)
        val spinner        = view.findViewById<Spinner>(R.id.spinnerCategory)
        val btnSave        = view.findViewById<Button>(R.id.btnSaveExpense)
        val tvError        = view.findViewById<TextView>(R.id.tvExpenseError)

        // Set up spinner
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            categories
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        btnSave.setOnClickListener {
            val amountStr   = etAmount.text.toString().trim()
            val description = etDescription.text.toString().trim()

            if (amountStr.isEmpty()) {
                tvError.text = "Please enter an amount"
                tvError.visibility = View.VISIBLE
                return@setOnClickListener
            }

            val amount = amountStr.toDoubleOrNull()
            if (amount == null || amount <= 0) {
                tvError.text = "Enter a valid amount greater than 0"
                tvError.visibility = View.VISIBLE
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val db = AppDatabase.getDatabase(requireContext())
                val categoryId = spinner.selectedItemPosition + 1

                // Run database operation on IO thread
                withContext(Dispatchers.IO) {
                    db.expenseDao().insertExpense(
                        Expense(
                            userId      = userId,
                            categoryId  = categoryId,
                            amount      = amount,
                            description = description,
                            date        = Calendar.getInstance().timeInMillis
                        )
                    )
                }

                // Back on Main thread to update UI
                onExpenseAdded()
                dismiss()
            }
        }
    }
}