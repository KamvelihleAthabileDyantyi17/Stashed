package com.example.stashed.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.stashed.StashedApplication
import com.example.stashed.utils.SessionManager

/**
 * Runs once a month (scheduled by StashedApplication) to clear the
 * current-month expense slate so budget progress bars reset to zero.
 *
 * Strategy: this worker simply triggers a "no-op" — the repository
 * already scopes all expense queries to the current calendar month,
 * so old expenses naturally stop appearing in dashboards on the 1st.
 * Real deletion of old data can be added here if retention limits are needed.
 */
class MonthlyResetWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val sessionManager = SessionManager(context)
            val userId = sessionManager.getUserId()

            if (userId != -1) {
                val app = context.applicationContext as StashedApplication
                // Repository already filters by month — future: archive old expenses
                // For now, mark work as succeeded; dashboards refresh via LiveData.
                app.repository.pruneOldExpenses(userId)
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
