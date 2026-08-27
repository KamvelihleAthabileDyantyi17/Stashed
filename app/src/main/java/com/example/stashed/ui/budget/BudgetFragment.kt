package com.example.stashed.ui.budget

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stashed.StashedApplication
import com.example.stashed.databinding.FragmentBudgetsBinding
import com.example.stashed.ui.ViewModelFactory
import com.example.stashed.utils.SessionManager
import com.github.mikephil.charting.data.*

class BudgetsFragment : Fragment() {

    private var _binding: FragmentBudgetsBinding? = null
    private val binding get() = _binding!!

    private lateinit var sessionManager: SessionManager
    private lateinit var categoryAdapter: BudgetCategoryAdapter

    // Safely initialize the ViewModel using the Factory and the logged-in user's ID
    private val viewModel: BudgetViewModel by viewModels {
        ViewModelFactory(
            (requireActivity().application as StashedApplication).repository,
            sessionManager.getUserId()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize SessionManager before the ViewModel tries to fetch the user ID
        sessionManager = SessionManager(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBudgetsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCharts() // Sets up the static bar chart & base chart configs
        setupRecyclerView()
        observeData()
    }

    private fun setupRecyclerView() {
        categoryAdapter = BudgetCategoryAdapter(
            onEditLimit = { category ->
                // TODO: Open SetBudgetDialog to edit the budget limit
            },
            onDelete = { category ->
                // Deletes the category via the ViewModel
                viewModel.deleteCategory(category)
            }
        )

        binding.rvBudgetCategories.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = categoryAdapter
            isNestedScrollingEnabled = false // Important for smooth scrolling inside a ScrollView
        }
    }

    private fun observeData() {
        // Observe categories from RoomDB via the ViewModel
        viewModel.categories.observe(viewLifecycleOwner) { categoryList ->

            // 1. Feed the database list directly to your RecyclerView adapter
            categoryAdapter.submitList(categoryList)

            // 2. Dynamically update your Pie Chart based on category budgets
            if (categoryList.isNotEmpty()) {
                val pieEntries = categoryList.map { cat ->
                    // Make sure budgetLimit is used here, or default to 0 if not set
                    PieEntry(cat.budgetLimit.toFloat(), cat.name)
                }

                val pieDataSet = PieDataSet(pieEntries, "Budget Limits").apply {
                    colors = listOf(
                        Color.parseColor("#E2B13C"),
                        Color.parseColor("#3C91E6"),
                        Color.parseColor("#E26D5C"),
                        Color.parseColor("#7232F2"),
                        Color.parseColor("#4EBA6F")
                    )
                    valueTextSize = 11f
                    valueTextColor = Color.parseColor("#F4F0E6")
                }

                binding.pieChartBudget.apply {
                    data = PieData(pieDataSet)
                    invalidate() // Refreshes the chart with live data!
                }
            }
        }
    }

    private fun setupCharts() {
        // --- Base Configuration for Pie Chart ---
        binding.pieChartBudget.apply {
            description.isEnabled = false
            isDrawHoleEnabled = true
            setHoleColor(Color.TRANSPARENT)
            setTransparentCircleColor(Color.TRANSPARENT)
            setCenterText("Expenses")
            setCenterTextColor(Color.parseColor("#8B8A8E"))
            setCenterTextSize(12f)
            legend.textColor = Color.parseColor("#F4F0E6")
        }

        // --- Configure Bar Chart (Currently keeping dummy data for layout testing) ---
        val barEntries = listOf(
            BarEntry(1f, 400f),
            BarEntry(2f, 650f),
            BarEntry(3f, 300f),
            BarEntry(4f, 900f),
            BarEntry(5f, 500f)
        )

        val barDataSet = BarDataSet(barEntries, "Daily Spending").apply {
            color = Color.parseColor("#E2B13C")
            valueTextColor = Color.parseColor("#F4F0E6")
            valueTextSize = 10f
        }

        binding.barChartTrends.apply {
            data = BarData(barDataSet)
            description.isEnabled = false
            setDrawGridBackground(false)

            xAxis.textColor = Color.parseColor("#8B8A8E")
            xAxis.setDrawGridLines(false)

            axisLeft.textColor = Color.parseColor("#8B8A8E")
            axisLeft.setDrawGridLines(true)
            axisLeft.gridColor = Color.parseColor("#232326")

            axisRight.isEnabled = false
            legend.textColor = Color.parseColor("#F4F0E6")
            invalidate()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}