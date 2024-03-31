package com.example.skyalert.dataSource.local.db.model

import com.example.skyalert.services.alarm.model.Alert

sealed class AlertsState {
    object Loading : AlertsState()
    data class Success(val alerts: MutableList<Alert>) : AlertsState()
    data class Error(val message: String) : AlertsState()
}