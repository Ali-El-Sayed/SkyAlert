package com.example.skyalert.services.alarm

import com.example.skyalert.services.alarm.model.Alert

interface AlarmScheduler {
    fun scheduleAlarm(item: Alert)
    fun cancelAlarm(item: Alert)
}