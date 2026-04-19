package com.example.stashed.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.stashed.R

object NotificationHelper {

    const val CHANNEL_BUDGET = "stashed_budget_alerts"
    private const val NOTIFICATION_ID_BASE_WARNING = 1000
    private const val NOTIFICATION_ID_BASE_EXCEEDED = 2000

    fun createChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_BUDGET,
                "Budget Alerts",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Alerts when you approach or exceed your category budget limits."
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    fun sendBudgetWarning(context: Context, categoryName: String, categoryId: Int) {
        val notification = NotificationCompat.Builder(context, CHANNEL_BUDGET)
            .setSmallIcon(R.drawable.ic_budget_alert)
            .setContentTitle("Budget Alert — $categoryName")
            .setContentText("You've used 80% of your $categoryName budget this month.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context)
                .notify(NOTIFICATION_ID_BASE_WARNING + categoryId, notification)
        } catch (_: SecurityException) { /* permission not granted */ }
    }

    fun sendBudgetExceeded(context: Context, categoryName: String, categoryId: Int) {
        val notification = NotificationCompat.Builder(context, CHANNEL_BUDGET)
            .setSmallIcon(R.drawable.ic_budget_alert)
            .setContentTitle("Over Budget — $categoryName")
            .setContentText("You have exceeded your $categoryName budget for this month!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context)
                .notify(NOTIFICATION_ID_BASE_EXCEEDED + categoryId, notification)
        } catch (_: SecurityException) { /* permission not granted */ }
    }
}
