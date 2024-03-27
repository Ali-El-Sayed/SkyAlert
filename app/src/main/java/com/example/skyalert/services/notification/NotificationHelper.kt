package com.example.skyalert.services.notification

import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import androidx.core.app.NotificationCompat

object NotificationHelper {
    fun createNotification(
        context: Context,
        title: String,
        description: String,
        channelId: String,
        icon: Bitmap,
        priority: Int
    ) {
        val notification = NotificationCompat.Builder(context, channelId).setContentTitle(title)
            .setContentText(description).setSmallIcon(android.R.drawable.ic_dialog_info)
            .setLargeIcon(icon).setPriority(priority).build()
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, notification)
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