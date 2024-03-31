package com.example.skyalert.view.screens.alerts.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skyalert.dataSource.local.db.model.AlertsState
import com.example.skyalert.repository.IWeatherRepo
import com.example.skyalert.services.alarm.model.Alert
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "AlertViewmodel"

class AlertViewmodel(private val repo: IWeatherRepo) : ViewModel() {
    private val _alerts: MutableStateFlow<AlertsState> = MutableStateFlow(AlertsState.Loading)
    val alerts = _alerts.asStateFlow()

    fun getAlerts() {
        viewModelScope.launch(Dispatchers.IO) {
            _alerts.value = AlertsState.Loading
            repo.getAllAlerts().collect {
                when (it) {
                    is AlertsState.Success -> {
                        val alerts = it.alerts.filter { alert ->
                            System.currentTimeMillis() - alert.time < 0
                        }
                        if (alerts.isEmpty()) _alerts.value = AlertsState.Success(mutableListOf())
                        else _alerts.value = AlertsState.Success(alerts.toMutableList())
                    }

                    is AlertsState.Loading -> {
                        _alerts.value = AlertsState.Loading
                    }

                    is AlertsState.Error -> {
                        Log.d(TAG, "getAlerts: ${it.message}")
                        _alerts.value = AlertsState.Error(it.message)
                    }
                }
            }
        }
    }

    suspend fun deleteAlert(alert: Alert) {
        viewModelScope.launch(Dispatchers.IO) { repo.deleteAlert(alert) }
    }

}