package com.example.skyalert.services.alarm

import com.example.skyalert.services.alarm.model.AlarmItem

interface AlarmScheduler {
    fun scheduleAlarm(item: AlarmItem)
    fun cancelAlarm(item: AlarmItem)
}