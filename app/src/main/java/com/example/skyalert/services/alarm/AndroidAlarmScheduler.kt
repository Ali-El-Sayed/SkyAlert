package com.example.skyalert.services.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.skyalert.services.alarm.model.AlarmItem
import com.example.skyalert.services.broadcastReceiver.alarmReceiver.AlarmReceiver
import java.time.ZoneId

class AndroidAlarmScheduler(private val context: Context) : AlarmScheduler {
    private val alarmManager: AlarmManager by lazy {
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    override fun scheduleAlarm(item: AlarmItem) {
        val i = Intent(context, AlarmReceiver::class.java)
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
        Toast.makeText(context, "Alarm set", Toast.LENGTH_SHORT).show()
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
        Toast.makeText(context, "Alarm canceled", Toast.LENGTH_SHORT).show()
    }
}