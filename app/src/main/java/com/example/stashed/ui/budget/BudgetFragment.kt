package com.example.stashed.ui.budget

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stashed.R
import com.example.stashed.StashedApplication
import com.example.stashed.data.entities.Category
import com.example.stashed.data.entities.Expense
import com.example.stashed.databinding.FragmentBudgetsBinding
import com.example.stashed.ui.ViewModelFactory
import com.example.stashed.utils.SessionManager
import com.github.mikephil.charting.data.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText

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

        setupCharts()
        setupRecyclerView()
        observeData()

        // Handle the FAB click to add a new category!
        binding.fabAddCategory.setOnClickListener {
            showCategoryDialog(null) // null means we are adding a NEW category
        }
    }

    private fun setupRecyclerView() {
        categoryAdapter = BudgetCategoryAdapter(
            onEditLimit = { category ->
                // Pass the existing category so the dialog knows we are EDITING
                showCategoryDialog(category)
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

    private fun showCategoryDialog(category: Category?) {
        // Inflates the custom dialog layout we created earlier
        val dialogView = layoutInflater.inflate(R.layout.dialog_category, null)
        val etName = dialogView.findViewById<TextInputEditText>(R.id.etCategoryName)
        val etLimit = dialogView.findViewById<TextInputEditText>(R.id.etBudgetLimit)

        // If we are editing, pre-fill the fields with the current data
        if (category != null) {
            etName.setText(category.name)
            etLimit.setText(category.budgetLimit.toString())
        }

        val title = if (category == null) "New Category" else "Edit Budget"

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(title)
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val name = etName.text.toString()
                val limit = etLimit.text.toString().toDoubleOrNull() ?: 0.0

                if (category == null) {
                    viewModel.addCategory(name, limit) // Save new category to RoomDB
                } else {
                    viewModel.updateBudgetLimit(category, limit) // Update existing category in RoomDB
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun observeData() {
        // 1. Observe categories from RoomDB
        viewModel.categories.observe(viewLifecycleOwner) { categoryList ->
            categoryAdapter.submitList(categoryList)

            if (categoryList.isNotEmpty()) {
                val pieEntries = categoryList.map { cat ->
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
                    invalidate()
                }
            }
        }

        // 2. Observe the current month's expenses for the bar chart
        viewModel.currentMonthExpenses.observe(viewLifecycleOwner) { expenses ->
            updateBarChart(expenses)
        }
    }

    private fun updateBarChart(expenses: List<Expense>) {
        if (expenses.isEmpty()) {
            binding.barChartTrends.clear()
            return
        }

        // Group the expenses by the Day of the Month
        val calendar = java.util.Calendar.getInstance()
        val dailyTotals = mutableMapOf<Float, Float>()

        for (expense in expenses) {
            calendar.timeInMillis = expense.date
            val day = calendar.get(java.util.Calendar.DAY_OF_MONTH).toFloat()
            val currentTotal = dailyTotals[day] ?: 0f
            dailyTotals[day] = currentTotal + expense.amount.toFloat()
        }

        // Convert our grouped totals into BarEntries sorted by day
        val barEntries = dailyTotals.entries.sortedBy { it.key }.map {
            BarEntry(it.key, it.value)
        }

        val barDataSet = BarDataSet(barEntries, "Daily Spending").apply {
            color = Color.parseColor("#E2B13C")
            valueTextColor = Color.parseColor("#F4F0E6")
            valueTextSize = 10f
        }

        binding.barChartTrends.apply {
            data = BarData(barDataSet)

            // Format the X-Axis to look like days (e.g., Day 1, Day 15)
            xAxis.valueFormatter = object : com.github.mikephil.charting.formatter.ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return value.toInt().toString()
                }
            }

            invalidate() // Refresh the chart with live data!
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

        // --- Base Configuration for Bar Chart ---
        // (Dummy data removed! It is now handled dynamically by updateBarChart)
        binding.barChartTrends.apply {
            description.isEnabled = false
            setDrawGridBackground(false)

            xAxis.textColor = Color.parseColor("#8B8A8E")
            xAxis.setDrawGridLines(false)

            axisLeft.textColor = Color.parseColor("#8B8A8E")
            axisLeft.setDrawGridLines(true)
            axisLeft.gridColor = Color.parseColor("#232326")

            axisRight.isEnabled = false
            legend.textColor = Color.parseColor("#F4F0E6")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}