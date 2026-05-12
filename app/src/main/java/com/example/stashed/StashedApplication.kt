package com.example.stashed

import android.app.Application
import androidx.work.*
import com.example.stashed.data.AppDatabase
import com.example.stashed.data.repository.StashedRepository
import com.example.stashed.utils.NotificationHelper
import com.example.stashed.workers.BudgetCheckWorker
import java.util.concurrent.TimeUnit

class StashedApplication : Application() {

    val database by lazy { AppDatabase.getInstance(this) }

    val repository by lazy {
        StashedRepository(
            database.userDao(),
            database.expenseDao(),
            database.categoryDao(),
            database.goalDao()
        )
    }

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createChannels(this)
        scheduleBudgetCheckWorker()
    }

    private fun scheduleBudgetCheckWorker() {
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(false)
            .build()

        val budgetCheckRequest = PeriodicWorkRequestBuilder<BudgetCheckWorker>(
            1, TimeUnit.DAYS
        ).setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "budget_check",
            ExistingPeriodicWorkPolicy.KEEP,
            budgetCheckRequest
        )
    }
}
