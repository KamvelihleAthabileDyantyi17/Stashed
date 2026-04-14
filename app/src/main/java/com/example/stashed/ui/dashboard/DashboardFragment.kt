package com.example.stashed.ui.dashboard

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
import com.example.stashed.utils.CurrencyUtils
import com.example.stashed.utils.SessionManager

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var budgetAdapter: CategoryBudgetAdapter
    private lateinit var recentAdapter: RecentExpenseAdapter

    private val viewModel: DashboardViewModel by viewModels {
        val app = requireActivity().application as StashedApplication
        val userId = SessionManager(requireContext()).getUserId()
        ViewModelFactory(app.repository, userId)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvMonthLabel.text = viewModel.currentMonthLabel

        budgetAdapter = CategoryBudgetAdapter()
        binding.rvBudgets.layoutManager = LinearLayoutManager(requireContext())
        binding.rvBudgets.adapter = budgetAdapter

        recentAdapter = RecentExpenseAdapter(emptyMap())
        binding.rvRecentExpenses.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRecentExpenses.adapter = recentAdapter

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_addExpense)
        }

        viewModel.totalSpend.observe(viewLifecycleOwner) { total ->
            binding.tvTotalSpend.text = CurrencyUtils.format(total ?: 0.0)
        }

        viewModel.categoryItems.observe(viewLifecycleOwner) { items ->
            budgetAdapter.submitList(items)
            binding.tvEmptyBudgets.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.recentExpenses.observe(viewLifecycleOwner) { expenses ->
            val catMap = viewModel.categoryItems.value
                ?.associate { it.category.categoryId to it.category.name } ?: emptyMap()
            recentAdapter = RecentExpenseAdapter(catMap)
            binding.rvRecentExpenses.adapter = recentAdapter
            recentAdapter.submitList(expenses)
            binding.tvEmptyRecent.visibility = if (expenses.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadDashboard()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
