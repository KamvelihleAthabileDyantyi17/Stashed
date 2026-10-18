package com.example.stashed

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.stashed.data.AppDatabase
import com.example.stashed.data.entities.BudgetGoal
import com.example.stashed.data.entities.Expense
import com.example.stashed.ui.AddExpenseDialog
import com.example.stashed.ui.BadgeCelebrationActivity
import com.example.stashed.ui.BadgesActivity
import com.example.stashed.ui.GraphActivity
import com.example.stashed.ui.LoginActivity
import com.example.stashed.ui.SetBudgetDialog
import com.example.stashed.ui.adapters.TransactionAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

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
    private lateinit var btnViewBadges: Button
    private lateinit var btnCompleteMonth: Button
    private lateinit var btnViewGraph: Button

    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var transactionAdapter: TransactionAdapter
    private var userId = -1
    private var currentBudget: BudgetGoal? = null
    private lateinit var tvGoalStatus: TextView
    private lateinit var progressGoal: ProgressBar
    private lateinit var tvMinGoalLabel: TextView
    private lateinit var tvMaxGoalLabel: TextView

    private val CHANNEL_ID = "BudgetAlerts"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        sharedPrefs = getSharedPreferences("StashedSession", MODE_PRIVATE)
        userId = sharedPrefs.getInt("userId", -1)

        if (userId == -1) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        createNotificationChannel()
        bindViews()
        setupRecyclerView()
        setupClickListeners()

        val fullName  = sharedPrefs.getString("fullName", "there") ?: "there"
        val firstName = fullName.split(" ").firstOrNull() ?: fullName
        tvGreeting.text = "Hello, $firstName 👋"
        tvMonth.text    = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Date())

        android.util.Log.d("MainActivity", "Loading dashboard for userId: $userId")

        // Trigger the background Firebase sync on startup
        syncOfflineExpensesToFirebase()
        loadDashboard()
    }

    override fun onResume() {
        super.onResume()
        loadDashboard()
    }

    // --- FIREBASE OFFLINE SYNC REQUIREMENT ---
    private fun syncOfflineExpensesToFirebase() {
        val firestore = FirebaseFirestore.getInstance()

        lifecycleScope.launch {
            val db = AppDatabase.getDatabase(applicationContext)
            val unsyncedExpenses = db.expenseDao().getUnsyncedExpenses()

            for (expense in unsyncedExpenses) {
                val expenseMap = hashMapOf(
                    "userId" to expense.userId,
                    "amount" to expense.amount,
                    "description" to expense.description,
                    "category" to expense.category,
                    "date" to expense.date
                )

                firestore.collection("Expenses").add(expenseMap)
                    .addOnSuccessListener {
                        lifecycleScope.launch {
                            val syncedExpense = expense.copy(isSynced = true)
                            db.expenseDao().updateExpense(syncedExpense)
                            android.util.Log.d("SYNC", "Successfully synced: ${expense.description}")
                        }
                    }
                    .addOnFailureListener { e ->
                        android.util.Log.e("SYNC", "Failed to sync: ${expense.description}", e)
                    }
            }
        }
    }

    // --- REAL-TIME NOTIFICATIONS REQUIREMENT ---
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Budget Alerts"
            val descriptionText = "Notifications for when you exceed your budget"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendBudgetNotification() {
        // Handle Android 13+ permission requirements
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
                return
            }
        }

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher) // Uses your new custom icon!
            .setContentTitle("Budget Alert ⚠️")
            .setContentText("You have exceeded your monthly maximum budget limit!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(this)) {
            notify(1001, builder.build())
        }
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
        btnViewBadges     = findViewById(R.id.btnViewBadges)
        btnCompleteMonth  = findViewById(R.id.btnCompleteMonth)
        btnViewGraph      = findViewById(R.id.btnViewGraph)
        tvGoalStatus    = findViewById(R.id.tvGoalStatus)
        progressGoal    = findViewById(R.id.progressGoal)
        tvMinGoalLabel  = findViewById(R.id.tvMinGoalLabel)
        tvMaxGoalLabel  = findViewById(R.id.tvMaxGoalLabel)
    }

    private fun setupRecyclerView() {
        transactionAdapter = TransactionAdapter(emptyList()) { expense ->
            lifecycleScope.launch {
                val db = AppDatabase.getDatabase(applicationContext)
                db.expenseDao().deleteExpense(expense)
                android.util.Log.d("MainActivity", "Deleted expense: ${expense.description}")
                runOnUiThread { loadDashboard() }
            }
        }
        rvTransactions.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = transactionAdapter
            isNestedScrollingEnabled = false
        }
    }

    private fun setupClickListeners() {
        btnViewBadges.setOnClickListener { startActivity(Intent(this, BadgesActivity::class.java)) }
        btnCompleteMonth.setOnClickListener { startActivity(Intent(this, BadgeCelebrationActivity::class.java)) }
        btnViewGraph.setOnClickListener { startActivity(Intent(this, GraphActivity::class.java)) }

        fabAddExpense.setOnClickListener {
            AddExpenseDialog(userId) { loadDashboard() }.show(supportFragmentManager, "AddExpense")
        }

        btnSetBudget.setOnClickListener {
            SetBudgetDialog(userId = userId, currentBudget = currentBudget?.maximumGoal ?: 0.0, currentIncome = 0.0) {
                loadDashboard()
            }.show(supportFragmentManager, "SetBudget")
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
        val income = sharedPrefs.getFloat("monthlyIncome", 0f).toDouble()

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
            val db         = AppDatabase.getDatabase(applicationContext)
            val budget     = db.budgetDao().getBudgetForMonth(userId, month, year)
            val totalSpent = db.expenseDao().getTotalSpendForMonth(userId, startCal.timeInMillis, endCal.timeInMillis)
            val recentList = db.expenseDao().getRecentExpenses(userId, 10).first()

            currentBudget = budget

            runOnUiThread {
                updateBudgetCard(budget, totalSpent, income)
                updateTransactionList(recentList)
            }
        }
    }

    private fun updateBudgetCard(budget: BudgetGoal?, totalSpent: Double, income: Double = 0.0) {
        val budgetAmount = budget?.maximumGoal ?: 0.0
        val remaining    = budgetAmount - totalSpent

        tvBudgetAmount.text    = "R%.2f".format(budgetAmount)
        tvSpentAmount.text     = "R%.2f".format(totalSpent)
        tvIncomeAmount.text = "R%.2f".format(income)
        tvRemainingAmount.text = if (remaining >= 0) "R%.2f".format(remaining) else "-R%.2f".format(-remaining)

        tvRemainingAmount.setTextColor(
            if (remaining < 0) getColor(android.R.color.holo_red_light) else getColor(android.R.color.holo_green_light)
        )

        if (budgetAmount > 0) {
            val percent = ((totalSpent / budgetAmount) * 100).toInt().coerceIn(0, 100)
            progressBudget.progress = percent
            tvProgressPercent.text  = "$percent% used"

            if (percent >= 100) {
                tvBudgetWarning.visibility = View.VISIBLE
                tvBudgetWarning.text = "Over budget!"
                // TRIGGER THE PUSH NOTIFICATION HERE!
                sendBudgetNotification()
            } else if (percent >= 80) {
                tvBudgetWarning.visibility = View.VISIBLE
                tvBudgetWarning.text = "Close to limit!"
            } else {
                tvBudgetWarning.visibility = View.GONE
            }
        } else {
            progressBudget.progress    = 0
            tvProgressPercent.text     = "No budget set"
            tvBudgetWarning.visibility = View.GONE
        }

        val minGoal = budget?.minimumGoal ?: 0.0
        val maxGoal = budget?.maximumGoal ?: 0.0

        tvMinGoalLabel.text = "Min: R%.2f".format(minGoal)
        tvMaxGoalLabel.text = "Max: R%.2f".format(maxGoal)

        if (maxGoal > 0) {
            val goalPercent = ((totalSpent / maxGoal) * 100).toInt().coerceIn(0, 100)
            progressGoal.progress = goalPercent

            when {
                totalSpent > maxGoal -> {
                    tvGoalStatus.text = "⚠️ Over maximum budget!"
                    tvGoalStatus.setTextColor(getColor(android.R.color.holo_red_light))
                }
                totalSpent >= minGoal -> {
                    tvGoalStatus.text = "✅ Within your goal range"
                    tvGoalStatus.setTextColor(getColor(android.R.color.holo_green_light))
                }
                else -> {
                    tvGoalStatus.text = "📊 Below minimum goal — keep tracking"
                    tvGoalStatus.setTextColor(0xFFFFD700.toInt())
                }
            }
        } else {
            tvGoalStatus.text = "Set a budget to see your progress"
            tvGoalStatus.setTextColor(0xFF888888.toInt())
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