package com.example.stashed.ui.budget

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stashed.R
import com.example.stashed.StashedApplication
import com.example.stashed.data.entities.Category
import com.example.stashed.databinding.FragmentBudgetsBinding
import com.example.stashed.ui.ViewModelFactory
import com.example.stashed.utils.CurrencyUtils
import com.example.stashed.utils.SessionManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import android.view.inputmethod.EditorInfo

class BudgetFragment : Fragment() {

    private var _binding: FragmentBudgetsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BudgetViewModel by viewModels {
        val app = requireActivity().application as StashedApplication
        val userId = SessionManager(requireContext()).getUserId()
        ViewModelFactory(app.repository, userId)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBudgetsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = BudgetCategoryAdapter(
            onEditLimit = { cat -> showEditLimitDialog(cat) },
            onDelete = { cat -> viewModel.deleteCategory(cat) }
        )
        binding.rvCategories.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCategories.adapter = adapter

        viewModel.categories.observe(viewLifecycleOwner) { cats ->
            adapter.submitList(cats)
            binding.tvEmpty.visibility = if (cats.isEmpty()) View.VISIBLE else View.GONE
        }

        binding.fabAddCategory.setOnClickListener {
            findNavController().navigate(R.id.action_budgets_to_addCategory)
        }

        viewModel.error.observe(viewLifecycleOwner) { msg ->
            if (msg != null) {
                Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }
    }

    private fun showEditLimitDialog(category: Category) {
        val input = TextInputEditText(requireContext())
        input.hint = "Budget limit (R)"
        input.inputType = EditorInfo.TYPE_CLASS_NUMBER or EditorInfo.TYPE_NUMBER_FLAG_DECIMAL
        input.setText(CurrencyUtils.formatShort(category.budgetLimit))

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Update budget for ${category.name}")
            .setView(input)
            .setPositiveButton("Save") { _, _ ->
                val newLimit = input.text.toString().toDoubleOrNull() ?: return@setPositiveButton
                viewModel.updateBudgetLimit(category, newLimit)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
