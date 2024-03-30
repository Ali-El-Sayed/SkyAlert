package com.example.skyalert.services.notification

import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import androidx.core.app.NotificationCompat
import kotlin.random.Random

object NotificationHelper {
    const val ALERT_CHANNEL_NAME: String = "Alert"
    const val ALERT_CHANNEL_ID = "alert"
    const val WEATHER_CHANNEL_DESCRIPTION = "Weather notification"
    fun createNotification(
        context: Context,
        title: String,
        description: String,
        details: String = "",
        channelId: String,
        icon: Bitmap,
        priority: Int
    ) {
        val notificationId = Random.nextInt()

        val notification = NotificationCompat.Builder(context, channelId).setContentTitle(title)
            .setContentText(description).setSmallIcon(android.R.drawable.ic_dialog_info)
            .setShowWhen(true).setStyle(NotificationCompat.BigTextStyle().bigText(details))
            .setLargeIcon(icon).setPriority(priority).build()
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, notification)
    }

    fun createNotificationChannel(
        context: Context,
        channelId: String,
        channelName: String,
        channelDescription: String,
        importance: Int
    ) {
        val channel = android.app.NotificationChannel(channelId, channelName, importance).apply {
            description = channelDescription
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}