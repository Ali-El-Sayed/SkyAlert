package com.example.skyalert.services.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.skyalert.services.alarm.model.AlarmItem
import com.example.skyalert.services.broadcastReceiver.alarmReceiver.AlarmReceiver
import java.time.ZoneId

class AndroidAlarmScheduler(private val context: Context) : AlarmScheduler {
    private val TAG = "AndroidAlarmScheduler"
    private val alarmManager: AlarmManager by lazy {
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    override fun scheduleAlarm(item: AlarmItem) {
        val i = Intent(context, AlarmManager::class.java).apply {
            putExtra("message", item.message)
        }
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            item.time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
            PendingIntent.getBroadcast(
                context,
                item.hashCode(),
                i,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }

    override fun cancelAlarm(item: AlarmItem) {
        alarmManager.cancel(
            PendingIntent.getBroadcast(
                context,
                item.hashCode(),
                Intent(context, AlarmReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }
}