package com.example.skyalert.interfaces

import com.example.skyalert.services.alarm.model.Alert

interface OnAlertClickListener {
    fun onAlertClick(alert: Alert)
}