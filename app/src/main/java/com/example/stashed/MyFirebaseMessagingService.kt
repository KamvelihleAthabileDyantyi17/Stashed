package com.example.stashed

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Extract title and body from the Firebase cloud message
        val title = remoteMessage.notification?.title ?: "Stashed Alert"
        val body = remoteMessage.notification?.body ?: "You have a new update."

        sendNotification(title, body)
    }

    private fun sendNotification(title: String, messageBody: String) {
        val channelId = "CloudAlerts"

        // Create the NotificationChannel (Required for Android 8.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Cloud Alerts", NotificationManager.IMPORTANCE_HIGH)
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        // Build the notification
        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher) // Uses your custom gold icon
            .setContentTitle(title)
            .setContentText(messageBody)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        // Show the notification if permissions are granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                NotificationManagerCompat.from(this).notify(System.currentTimeMillis().toInt(), builder.build())
            }
        } else {
            NotificationManagerCompat.from(this).notify(System.currentTimeMillis().toInt(), builder.build())
        }
    }

    override fun onNewToken(token: String) {
        android.util.Log.d("FCM", "Refreshed token: $token")
        // If you were sending messages to specific users, you would save this token to Firestore here.
    }
}