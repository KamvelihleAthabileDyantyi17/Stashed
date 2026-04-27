package com.example.stashed.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.lifecycleScope
import com.example.stashed.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.example.stashed.data.AppDatabase // FIXED IMPORT
import com.example.stashed.data.entities.BudgetGoal
import kotlinx.coroutines.launch
import java.util.Calendar

class SetBudgetDialog(
    private val userId: Int,
    private val currentBudget: Double,
    private val currentIncome: Double,
    private val onBudgetSet: () -> Unit
) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.dialog_set_budget, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etBudget = view.findViewById<EditText>(R.id.etMonthlyBudget)
        val etIncome = view.findViewById<EditText>(R.id.etMonthlyIncome)
        val btnSave  = view.findViewById<Button>(R.id.btnSaveBudget)
        val tvError  = view.findViewById<TextView>(R.id.tvBudgetError)

        // Pre-fill existing values
        if (currentBudget > 0) etBudget.setText(currentBudget.toInt().toString())
        if (currentIncome > 0) etIncome.setText(currentIncome.toInt().toString())

        btnSave.setOnClickListener {
            val budgetStr = etBudget.text.toString().trim()
            val incomeStr = etIncome.text.toString().trim()

            if (budgetStr.isEmpty()) {
                tvError.text = "Please enter a monthly budget"
                tvError.visibility = View.VISIBLE
                return@setOnClickListener
            }

            val budget = budgetStr.toDoubleOrNull()
            if (budget == null || budget <= 0) {
                tvError.text = "Enter a valid budget amount"
                tvError.visibility = View.VISIBLE
                return@setOnClickListener
            }

            val income = incomeStr.toDoubleOrNull() ?: 0.0
            val cal    = Calendar.getInstance()

            lifecycleScope.launch {
                val db = AppDatabase.getDatabase(requireContext())
                db.budgetDao().upsertBudget(
                    BudgetGoal(
                        userId      = userId,
                        maximumGoal = budget,
                        minimumGoal = 0.0,
                        month       = cal.get(Calendar.MONTH) + 1,
                        year        = cal.get(Calendar.YEAR)
                    )
                )

                requireActivity().runOnUiThread {
                    onBudgetSet()
                    dismiss()
                }
            }
        }
    }
}