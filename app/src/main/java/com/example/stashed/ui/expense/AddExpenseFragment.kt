package com.example.stashed.ui.expense

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.stashed.StashedApplication
import com.example.stashed.databinding.FragmentAddExpenseBinding
import com.example.stashed.ui.ViewModelFactory
import com.example.stashed.utils.SessionManager

class AddExpenseFragment : Fragment() {

    private var _binding: FragmentAddExpenseBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ExpenseViewModel by viewModels {
        val app = requireActivity().application as StashedApplication
        val userId = SessionManager(requireContext()).getUserId()
        // Ensure ViewModelFactory is updated if it complains about Int vs String later
        ViewModelFactory(app.repository, userId)
    }

    private var selectedCategoryId: Int = -1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAddExpenseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            // Temporarily default to the first category so the app compiles and saves correctly.
            // The clickable horizontal UI chips will be wired up once the app boots.
            if (categories.isNotEmpty()) {
                selectedCategoryId = categories[0].categoryId
            }
        }

        // Updated to use the new XML IDs
        binding.btnSaveExpense.setOnClickListener {
            val amountStr = binding.etExpenseAmount.text.toString().trim()
            val amount = amountStr.toDoubleOrNull() ?: 0.0
            val note = binding.etNote.text.toString().trim()

            if (selectedCategoryId != -1) {
                viewModel.addExpense(selectedCategoryId, amount, note)
            } else {
                Toast.makeText(requireContext(), "No category selected", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnCancel.setOnClickListener { findNavController().popBackStack() }

        viewModel.saveResult.observe(viewLifecycleOwner) { saved ->
            if (saved == true) {
                Toast.makeText(requireContext(), "Expense added!", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
                viewModel.clearSaveResult()
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { msg ->
            if (msg != null) {
                Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}