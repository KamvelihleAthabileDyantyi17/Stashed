package com.example.stashed.ui.expense

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.stashed.R
import com.example.stashed.StashedApplication
import com.example.stashed.data.entities.Category
import com.example.stashed.ui.ViewModelFactory
import com.example.stashed.utils.SessionManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class AddExpenseBottomSheet : BottomSheetDialogFragment() {

    private lateinit var sessionManager: SessionManager
    private var selectedCategoryId: Int = -1
    private var categoriesList: List<Category> = emptyList()

    private val viewModel: ExpenseViewModel by viewModels {
        ViewModelFactory(
            (requireActivity().application as StashedApplication).repository,
            sessionManager.getUserId()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionManager = SessionManager(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_add_expense, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etAmount = view.findViewById<TextInputEditText>(R.id.etExpenseAmount)
        val etNote = view.findViewById<TextInputEditText>(R.id.etExpenseNote)
        val spinnerCategory = view.findViewById<AutoCompleteTextView>(R.id.spinnerCategory)
        val btnSave = view.findViewById<MaterialButton>(R.id.btnSaveExpense)

        // 1. Observe categories and populate the dropdown menu
        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            categoriesList = categories
            val categoryNames = categories.map { it.name }

            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                categoryNames
            )
            spinnerCategory.setAdapter(adapter)
        }

        // 2. Save the category ID when the user taps an option in the dropdown
        spinnerCategory.setOnItemClickListener { _, _, position, _ ->
            selectedCategoryId = categoriesList[position].categoryId
        }

        // 3. Handle the Save button
        btnSave.setOnClickListener {
            val amountStr = etAmount.text.toString()
            val note = etNote.text.toString()
            val amount = amountStr.toDoubleOrNull() ?: 0.0

            viewModel.addExpense(selectedCategoryId, amount, note)
        }

        // 4. Listen for success or errors from your ViewModel!
        viewModel.saveResult.observe(viewLifecycleOwner) { success ->
            if (success == true) {
                Toast.makeText(requireContext(), "Expense logged!", Toast.LENGTH_SHORT).show()
                viewModel.clearSaveResult()
                dismiss() // Closes the bottom sheet automatically
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage != null) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                viewModel.clearError()
            }
        }
    }
}