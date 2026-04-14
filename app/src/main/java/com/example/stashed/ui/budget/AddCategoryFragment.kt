package com.example.stashed.ui.budget

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.stashed.StashedApplication
import com.example.stashed.databinding.FragmentAddCategoryBinding
import com.example.stashed.ui.ViewModelFactory
import com.example.stashed.utils.SessionManager

class AddCategoryFragment : Fragment() {

    private var _binding: FragmentAddCategoryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BudgetViewModel by viewModels {
        val app = requireActivity().application as StashedApplication
        val userId = SessionManager(requireContext()).getUserId()
        ViewModelFactory(app.repository, userId)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAddCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSave.setOnClickListener {
            val name = binding.etCategoryName.text.toString().trim()
            val limitStr = binding.etBudgetLimit.text.toString().trim()
            val limit = limitStr.toDoubleOrNull() ?: 0.0
            viewModel.addCategory(name, limit)
        }

        binding.btnCancel.setOnClickListener { findNavController().popBackStack() }

        viewModel.saveResult.observe(viewLifecycleOwner) { saved ->
            if (saved == true) {
                Toast.makeText(requireContext(), "Category added!", Toast.LENGTH_SHORT).show()
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
