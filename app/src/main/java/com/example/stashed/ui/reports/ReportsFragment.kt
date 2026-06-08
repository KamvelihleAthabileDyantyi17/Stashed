package com.example.stashed.ui.reports

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.anychart.AnyChart
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.enums.Align
import com.anychart.enums.LegendLayout
import com.example.stashed.StashedApplication
import com.example.stashed.databinding.FragmentReportsBinding
import com.example.stashed.ui.ViewModelFactory
import com.example.stashed.utils.CurrencyUtils
import com.example.stashed.utils.SessionManager

// MPAndroidChart Imports
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
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

        // 1. Setup the static chart appearance (Limit lines, hiding axes)
        setupExpenseChart()

        viewModel.categorySpends.observe(viewLifecycleOwner) { spends ->
            if (spends.isEmpty()) {
                binding.tvEmpty.visibility = View.VISIBLE
                binding.anyChartPie.visibility = View.GONE
                binding.expenseBarChart.visibility = View.GONE // Updated ID
                return@observe
            }

            binding.tvEmpty.visibility = View.GONE
            binding.anyChartPie.visibility = View.VISIBLE
            binding.expenseBarChart.visibility = View.VISIBLE // Updated ID

            // ── Pie Chart (AnyChart) ───────────────────────────────────────
            val pie = AnyChart.pie()
            val pieData: List<DataEntry> = spends.map {
                ValueDataEntry(it.category.name, it.totalSpent)
            }
            pie.data(pieData)
            pie.title("Spending by Category")
            pie.labels().position("outside")
            pie.legend()
                .position("center-bottom")
                .itemsLayout(LegendLayout.HORIZONTAL_EXPANDABLE)
                .align(Align.CENTER)
            binding.anyChartPie.setChart(pie)

            // ── Bar Chart (MPAndroidChart) ─────────────────────────────────
            // 1. Convert the data into BarEntry objects
            val entries = ArrayList<BarEntry>()
            var xIndex = 0f

            for (spend in spends) {
                // BarEntry takes an X position (float) and a Y value (float amount)
                entries.add(BarEntry(xIndex, spend.totalSpent.toFloat()))
                xIndex += 1f
            }

            // 2. Put the entries into a Dataset
            val dataSet = BarDataSet(entries, "Amount (R)")
            dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList() // Give it some nice default colors

            // 3. Apply the dataset to the chart and refresh
            val barData = BarData(dataSet)
            binding.expenseBarChart.data = barData
            binding.expenseBarChart.invalidate() // This redraws the chart with the new data
        }

        viewModel.currentMonthExpenses.observe(viewLifecycleOwner) { expenses ->
            val total = expenses.sumOf { it.amount }
            binding.tvTotalSpend.text = CurrencyUtils.format(total)
            binding.tvTransactionCount.text = "${expenses.size} transactions"
        }
    }

    private fun setupExpenseChart() {
        // Create the Max Goal Line
        val maxBudgetLine = LimitLine(2000f, "Max Budget Limit").apply {
            lineWidth = 2f
            lineColor = Color.RED
            enableDashedLine(10f, 10f, 0f)
            labelPosition = LimitLine.LimitLabelPosition.RIGHT_TOP
            textSize = 10f
            textColor = Color.DKGRAY
        }

        // Create the Min Goal Line
        val minTargetLine = LimitLine(500f, "Minimum Target").apply {
            lineWidth = 2f
            lineColor = Color.GREEN
            labelPosition = LimitLine.LimitLabelPosition.RIGHT_BOTTOM
            textSize = 10f
            textColor = Color.DKGRAY
        }

        // Apply them to the chart
        binding.expenseBarChart.apply {
            axisLeft.addLimitLine(maxBudgetLine)
            axisLeft.addLimitLine(minTargetLine)
            axisLeft.axisMinimum = 0f
            axisRight.isEnabled = false
            description.isEnabled = false
            setFitBars(true)
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