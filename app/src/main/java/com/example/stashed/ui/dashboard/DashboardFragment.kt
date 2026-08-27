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

    // We only need the recent adapter now since the XML uses Quick Action Chips instead of a budget list
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

        // Setup the Recent Transactions RecyclerView
        recentAdapter = RecentExpenseAdapter(emptyMap())
        binding.rvRecentTransactions.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRecentTransactions.adapter = recentAdapter
        binding.rvRecentTransactions.isNestedScrollingEnabled = false

        // Hook up the FAB to navigate to your Expense flow
        binding.fabAdd.setOnClickListener {
            // Note: If you want to use the BottomSheet we made earlier,
            // you can replace this with: AddExpenseBottomSheet().show(childFragmentManager, "AddExpense")
            findNavController().navigate(R.id.action_dashboard_to_addExpense)
        }

        // Observe Total Spend and map it to your tvMainBalance
        viewModel.totalSpend.observe(viewLifecycleOwner) { total ->
            binding.tvMainBalance.text = CurrencyUtils.format(total ?: 0.0)
        }

        // Observe Recent Expenses
        viewModel.recentExpenses.observe(viewLifecycleOwner) { expenses ->
            // We use categoryItems just to map the Category ID to the Category Name for the adapter
            val catMap = viewModel.categoryItems.value
                ?.associate { it.category.categoryId to it.category.name } ?: emptyMap()

            recentAdapter = RecentExpenseAdapter(catMap)
            binding.rvRecentTransactions.adapter = recentAdapter
            recentAdapter.submitList(expenses)
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