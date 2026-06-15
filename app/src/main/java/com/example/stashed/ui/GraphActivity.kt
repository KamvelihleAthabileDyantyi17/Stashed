package com.example.stashed.ui

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.stashed.R
import com.example.stashed.data.AppDatabase
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import kotlinx.coroutines.launch
import java.util.Calendar

class GraphActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graph)

        val prefs: SharedPreferences = getSharedPreferences("StashedSession", MODE_PRIVATE)
        val userId  = prefs.getInt("userId", -1)
        val chart   = findViewById<BarChart>(R.id.barChart)
        val tvNoData = findViewById<TextView>(R.id.tvNoData)

        val cal    = Calendar.getInstance()
        val month  = cal.get(Calendar.MONTH) + 1
        val year   = cal.get(Calendar.YEAR)

        val startCal = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        }
        val endCal = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
            set(Calendar.HOUR_OF_DAY, 23); set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59); set(Calendar.MILLISECOND, 999)
        }

        lifecycleScope.launch {
            val db         = AppDatabase.getDatabase(applicationContext)
            val totalSpent = db.expenseDao().getTotalSpendForMonth(
                userId, startCal.timeInMillis, endCal.timeInMillis
            )
            val budget     = db.budgetDao().getBudgetForMonth(userId, month, year)

            runOnUiThread {
                if (totalSpent == 0.0) {
                    tvNoData.visibility = View.VISIBLE
                    chart.visibility    = View.GONE
                    return@runOnUiThread
                }

                val entries = listOf(BarEntry(0f, totalSpent.toFloat()))
                val dataSet = BarDataSet(entries, "Spent this month").apply {
                    color = 0xFF2196F3.toInt()
                    valueTextColor = 0xFFFFFFFF.toInt()
                    valueTextSize = 12f
                }

                chart.apply {
                    data = BarData(dataSet)
                    setBackgroundColor(0xFF1E1E1E.toInt())
                    description.isEnabled = false
                    legend.textColor = 0xFFFFFFFF.toInt()
                    axisLeft.textColor = 0xFFFFFFFF.toInt()
                    axisRight.isEnabled = false
                    xAxis.isEnabled = false

                    // Add min/max goal lines
                    budget?.let { b ->
                        if (b.minimumGoal > 0) {
                            val minLine = LimitLine(b.minimumGoal.toFloat(), "Min Goal").apply {
                                lineColor = 0xFF4CAF50.toInt()
                                textColor = 0xFF4CAF50.toInt()
                                lineWidth = 2f
                            }
                            axisLeft.addLimitLine(minLine)
                        }
                        val maxLine = LimitLine(b.maximumGoal.toFloat(), "Max Goal").apply {
                            lineColor = 0xFFFF3B30.toInt()
                            textColor = 0xFFFF3B30.toInt()
                            lineWidth = 2f
                        }
                        axisLeft.addLimitLine(maxLine)
                    }
                    invalidate()
                }
            }
        }
    }
}