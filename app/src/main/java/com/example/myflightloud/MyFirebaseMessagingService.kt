package com.example.myflightloud

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val CHANNEL_ID = "flight_price_drop_channel"

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Log message data for debugging
        Log.d("FCM", "From: ${remoteMessage.from}")
        remoteMessage.data.isNotEmpty().let {
            Log.d("FCM", "Message data payload: ${remoteMessage.data}")
            val flightName = remoteMessage.data["flightName"]
            val newPrice = remoteMessage.data["newPrice"]?.toDouble() ?: 0.0

            // Show notification
            sendNotification(flightName ?: "Unknown Flight", newPrice)
        }

        remoteMessage.notification?.let {
            Log.d("FCM", "Message Notification Body: ${it.body}")
        }
    }

    private fun sendNotification(flightName: String, newPrice: Double) {
        // Create Notification Channel (required for Android 8.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Flight Price Drop",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = "Notifies you when a flight price drops"
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        // Build the notification
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Price Drop Alert!")
            .setContentText("$flightName price dropped to $$newPrice")
            .setSmallIcon(android.R.drawable.ic_notification_overlay)  // Use your app icon here
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        // Show the notification
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, notification)
    }
}
