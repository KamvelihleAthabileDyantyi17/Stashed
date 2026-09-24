package com.example.stashed.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.stashed.StashedApplication
import com.example.stashed.utils.NotificationHelper
import com.example.stashed.utils.SessionManager

class BudgetCheckWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val session = SessionManager(context)
        if (!session.isLoggedIn()) return Result.success()

        val userId = session.getUserId()
        val app = context.applicationContext as StashedApplication
        val repo = app.repository

        val categories = repo.getCategoriesSync(userId)

        for (category in categories) {
            // FIXED: 'budgetLimit' was removed from the Category table.
            // Placeholder set to 0.0 until the new Budget table is wired up.
            val currentBudgetLimit = 0.0

            if (currentBudgetLimit <= 0) continue

            val spent = repo.getTotalForCategoryThisMonth(userId, category.categoryId)
            val percentage = (spent / currentBudgetLimit) * 100.0

            when {
                percentage >= 100.0 -> NotificationHelper.sendBudgetExceeded(
                    context, category.name, category.categoryId
                )
                percentage >= 80.0 -> NotificationHelper.sendBudgetWarning(
                    context, category.name, category.categoryId
                )
            }
        }

        return Result.success()
    }
}