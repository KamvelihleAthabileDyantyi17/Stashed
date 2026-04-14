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

        viewModel.categorySpends.observe(viewLifecycleOwner) { spends ->
            if (spends.isEmpty()) {
                binding.tvEmpty.visibility = View.VISIBLE
                binding.anyChartPie.visibility = View.GONE
                binding.anyChartBar.visibility = View.GONE
                return@observe
            }

            binding.tvEmpty.visibility = View.GONE
            binding.anyChartPie.visibility = View.VISIBLE
            binding.anyChartBar.visibility = View.VISIBLE

            // ── Pie Chart ──────────────────────────────────────────────────
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

            // ── Bar Chart ──────────────────────────────────────────────────
            val bar = AnyChart.bar()
            val barData: List<DataEntry> = spends.map {
                ValueDataEntry(it.category.name, it.totalSpent)
            }
            bar.data(barData)
            bar.title("Category Spend — This Month")
            bar.yAxis(0).title("Amount (R)")
            bar.xAxis(0).title("Category")
            binding.anyChartBar.setChart(bar)
        }

        viewModel.currentMonthExpenses.observe(viewLifecycleOwner) { expenses ->
            val total = expenses.sumOf { it.amount }
            binding.tvTotalSpend.text = CurrencyUtils.format(total)
            binding.tvTransactionCount.text = "${expenses.size} transactions"
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
