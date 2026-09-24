package com.example.stashed.ui.reports

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.stashed.StashedApplication
import com.example.stashed.databinding.FragmentReportsBinding
import com.example.stashed.ui.ViewModelFactory
import com.example.stashed.utils.CurrencyUtils
import com.example.stashed.utils.SessionManager
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate

class ReportsFragment : Fragment() {

    private var _binding: FragmentReportsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ReportsViewModel by viewModels {
        val app = requireActivity().application as StashedApplication
        val userId = SessionManager(requireContext()).getUserId()
        ViewModelFactory(app.repository, userId)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentReportsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupExpenseChart()

        viewModel.categorySpends.observe(viewLifecycleOwner) { spends ->
            if (spends.isEmpty()) {
                binding.tvEmpty.visibility = View.VISIBLE
                binding.chartCard.visibility = View.GONE
                return@observe
            }

            binding.tvEmpty.visibility = View.GONE
            binding.chartCard.visibility = View.VISIBLE

            // ── Bar Chart (MPAndroidChart) ─────────────────────────────────
            val entries = ArrayList<BarEntry>()
            val labels = ArrayList<String>()

            var xIndex = 0f

            for (spend in spends) {
                // BarEntry takes an X position (float) and a Y value (float amount)
                entries.add(BarEntry(xIndex, spend.totalSpent.toFloat()))
                labels.add(spend.category.name)
                xIndex += 1f
            }

            val dataSet = BarDataSet(entries, "Amount (R)")

            // Dark Mode Chart Styling
            dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
            dataSet.valueTextColor = Color.parseColor("#F4F0E6")
            dataSet.valueTextSize = 10f

            val barData = BarData(dataSet)
            binding.expenseBarChart.data = barData

            // Set X-Axis labels to category names
            binding.expenseBarChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)

            // Redraw the chart
            binding.expenseBarChart.invalidate()
        }

        viewModel.currentMonthExpenses.observe(viewLifecycleOwner) { expenses ->
            val total = expenses.sumOf { it.amount }
            binding.tvTotalSpend.text = CurrencyUtils.format(total)
            binding.tvTransactionCount.text = "${expenses.size}"
        }
    }

    private fun setupExpenseChart() {
        binding.expenseBarChart.apply {
            description.isEnabled = false
            setDrawGridBackground(false)
            setDrawBorders(false)

            // Legend styling
            legend.textColor = Color.parseColor("#8B8A8E")

            // X-Axis styling
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.textColor = Color.parseColor("#8B8A8E")
            xAxis.setDrawGridLines(false)
            xAxis.granularity = 1f

            // Y-Axis (Left) styling
            axisLeft.textColor = Color.parseColor("#8B8A8E")
            axisLeft.setDrawGridLines(true)
            axisLeft.gridColor = Color.parseColor("#232326")
            axisLeft.axisMinimum = 0f

            // Hide Right Y-Axis
            axisRight.isEnabled = false

            setFitBars(true)
            animateY(1000) // Add a nice animation when it loads
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadCategorySpends()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}