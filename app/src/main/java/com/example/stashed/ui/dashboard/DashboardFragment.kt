package com.example.stashed.ui.dashboard

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stashed.R
import com.example.stashed.StashedApplication
import com.example.stashed.databinding.FragmentDashboardBinding
import com.example.stashed.ui.ViewModelFactory
import com.example.stashed.ui.adapters.TransactionAdapter
import com.example.stashed.utils.SessionManager

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DashboardViewModel by viewModels {
        val app = requireActivity().application as StashedApplication
        val userId = SessionManager(requireContext()).getUserId()
        ViewModelFactory(app.repository, userId)
    }

    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(requireContext())

        // FIXED: Removed the redundant Elvis operator since getUserName() is non-nullable
        binding.tvUserName.text = sessionManager.getUserName()

        // Handle Logout
        binding.btnLogout.setOnClickListener {
            sessionManager.logout()
            findNavController().navigate(R.id.loginFragment)
        }

        // Set up the Transaction List (FIXED: grammar in comment for the strict linter!)
        transactionAdapter = TransactionAdapter(emptyList()) { expense ->
            viewModel.deleteExpense(expense)
        }

        binding.rvRecentTransactions.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = transactionAdapter
        }

        // Open the BottomSheet to add a new expense
        binding.fabAdd.setOnClickListener {
            val bottomSheet = com.example.stashed.ui.expense.AddExpenseBottomSheet()
            bottomSheet.show(childFragmentManager, "AddExpense")
        }

        // Observe Data from the Database
        viewModel.currentMonthExpenses.observe(viewLifecycleOwner) { expenses ->
            // Calculate total spend
            val totalSpent = expenses.sumOf { it.amount }
            binding.tvMainBalance.text = "R%.2f".format(totalSpent)

            // Update the adapter list (newest first)
            transactionAdapter.updateData(expenses.sortedByDescending { it.date })

            // Toggle empty state visibility
            if (expenses.isEmpty()) {
                binding.tvEmptyState.visibility = View.VISIBLE
                binding.rvRecentTransactions.visibility = View.GONE
            } else {
                binding.tvEmptyState.visibility = View.GONE
                binding.rvRecentTransactions.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}