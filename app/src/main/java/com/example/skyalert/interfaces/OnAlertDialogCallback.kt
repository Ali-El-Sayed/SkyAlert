package com.example.skyalert.interfaces

import androidx.work.OneTimeWorkRequest
import com.example.skyalert.services.alarm.model.AlarmItem

interface OnAlertDialogCallback {
    fun createNotification(alarmItem: AlarmItem)
    fun createDialog(request: OneTimeWorkRequest)
}