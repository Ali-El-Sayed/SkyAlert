package com.example.skyalert.interfaces

import androidx.work.OneTimeWorkRequest

interface OnAlertDialogCallback {
    fun createNotification()
    fun createDialog(request: OneTimeWorkRequest)
}