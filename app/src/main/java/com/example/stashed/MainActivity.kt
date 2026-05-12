package com.example.stashed

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.stashed.ui.AddExpenseDialog
import com.example.stashed.ui.SetBudgetDialog
import com.example.stashed.ui.adapters.TransactionAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton

// FIXED IMPORTS
import com.example.stashed.data.AppDatabase
import com.example.stashed.data.entities.BudgetGoal
import com.example.stashed.data.entities.Expense
import com.example.stashed.ui.LoginActivity

import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    // Views
    private lateinit var tvGreeting: TextView
    private lateinit var tvMonth: TextView
    private lateinit var tvBudgetAmount: TextView
    private lateinit var tvSpentAmount: TextView
    private lateinit var tvRemainingAmount: TextView
    private lateinit var tvIncomeAmount: TextView
    private lateinit var progressBudget: ProgressBar
    private lateinit var tvProgressPercent: TextView
    private lateinit var tvBudgetWarning: TextView
    private lateinit var rvTransactions: RecyclerView
    private lateinit var tvNoTransactions: TextView
    private lateinit var fabAddExpense: FloatingActionButton
    private lateinit var btnSetBudget: Button
    private lateinit var btnLogout: Button

    // State
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var transactionAdapter: TransactionAdapter
    private var userId = -1
    private var currentBudget: BudgetGoal? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        sharedPrefs = getSharedPreferences("StashedSession", MODE_PRIVATE)
        userId = sharedPrefs.getInt("userId", -1)

        // If not logged in go back to login
        if (userId == -1) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        bindViews()
        setupRecyclerView()
        setupClickListeners()

        // Greet the user
        val fullName  = sharedPrefs.getString("fullName", "there") ?: "there"
        val firstName = fullName.split(" ").firstOrNull() ?: fullName
        tvGreeting.text = "Hello, $firstName 👋"
        tvMonth.text    = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Date())

        loadDashboard()
    }

    override fun onResume() {
        super.onResume()
        loadDashboard()
    }

    private fun bindViews() {
        tvGreeting        = findViewById(R.id.tvGreeting)
        tvMonth           = findViewById(R.id.tvMonth)
        tvBudgetAmount    = findViewById(R.id.tvBudgetAmount)
        tvSpentAmount     = findViewById(R.id.tvSpentAmount)
        tvRemainingAmount = findViewById(R.id.tvRemainingAmount)
        tvIncomeAmount    = findViewById(R.id.tvIncomeAmount)
        progressBudget    = findViewById(R.id.progressBudget)
        tvProgressPercent = findViewById(R.id.tvProgressPercent)
        tvBudgetWarning   = findViewById(R.id.tvBudgetWarning)
        rvTransactions    = findViewById(R.id.rvRecentTransactions)
        tvNoTransactions  = findViewById(R.id.tvNoTransactions)
        fabAddExpense     = findViewById(R.id.fabAddExpense)
        btnSetBudget      = findViewById(R.id.btnSetBudget)
        btnLogout         = findViewById(R.id.btnLogout)
    }

    private fun setupRecyclerView() {
        transactionAdapter = TransactionAdapter(emptyList())
        rvTransactions.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = transactionAdapter
            isNestedScrollingEnabled = false
        }
    }

    private fun setupClickListeners() {

        fabAddExpense.setOnClickListener {
            AddExpenseDialog(userId) { loadDashboard() }
                .show(supportFragmentManager, "AddExpense")
        }

        btnSetBudget.setOnClickListener {
            SetBudgetDialog(
                userId        = userId,
                currentBudget = currentBudget?.maximumGoal ?: 0.0,
                currentIncome = 0.0
            ) { loadDashboard() }
                .show(supportFragmentManager, "SetBudget")
        }

        btnLogout.setOnClickListener {
            sharedPrefs.edit().clear().apply()
            startActivity(Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
            finish()
        }
    }

    private fun loadDashboard() {
        val cal   = Calendar.getInstance()
        val month = cal.get(Calendar.MONTH) + 1
        val year  = cal.get(Calendar.YEAR)

        val startCal = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val endCal = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }

        lifecycleScope.launch {
            // This call now works because of the import above
            val db         = AppDatabase.getDatabase(applicationContext)
            val budget     = db.budgetDao().getBudgetForMonth(userId, month, year)
            val totalSpent = db.expenseDao().getTotalSpentForMonth(
                userId, startCal.timeInMillis, endCal.timeInMillis
            )
            val recentList = db.expenseDao().getRecentExpenses(userId, 10)

            currentBudget = budget

            runOnUiThread {
                updateBudgetCard(budget, totalSpent)
                updateTransactionList(recentList)
            }
        }
    }

    private fun updateBudgetCard(budget: BudgetGoal?, totalSpent: Double) {
        val budgetAmount = budget?.maximumGoal ?: 0.0
        val remaining    = budgetAmount - totalSpent

        tvBudgetAmount.text    = "R%.2f".format(budgetAmount)
        tvSpentAmount.text     = "R%.2f".format(totalSpent)
        tvIncomeAmount.text    = "R0.00"
        tvRemainingAmount.text = if (remaining >= 0) "R%.2f".format(remaining)
        else "-R%.2f".format(-remaining)

        tvRemainingAmount.setTextColor(
            if (remaining < 0) getColor(android.R.color.holo_red_light)
            else getColor(android.R.color.holo_green_light)
        )

        if (budgetAmount > 0) {
            val percent = ((totalSpent / budgetAmount) * 100).toInt().coerceIn(0, 100)
            progressBudget.progress = percent
            tvProgressPercent.text  = "$percent% used"
            if (percent >= 80) {
                tvBudgetWarning.visibility = View.VISIBLE
                tvBudgetWarning.text = if (percent >= 100) "Over budget!" else "Close to limit!"
            } else {
                tvBudgetWarning.visibility = View.GONE
            }
        } else {
            progressBudget.progress    = 0
            tvProgressPercent.text     = "No budget set"
            tvBudgetWarning.visibility = View.GONE
        }
    }

    private fun updateTransactionList(expenses: List<Expense>) {
        if (expenses.isEmpty()) {
            tvNoTransactions.visibility = View.VISIBLE
            rvTransactions.visibility   = View.GONE
        } else {
            tvNoTransactions.visibility = View.GONE
            rvTransactions.visibility   = View.VISIBLE
            transactionAdapter.updateData(expenses)
        }
    }
}