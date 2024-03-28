package com.example.skyalert.services.broadcastReceiver.alarmReceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.skyalert.dataSource.local.sharedPref.SharedPreferenceImpl
import com.example.skyalert.dataSource.remote.WeatherRemoteDatasource
import com.example.skyalert.network.RetrofitClient
import com.example.skyalert.network.model.CurrentWeatherState
import com.example.skyalert.repository.WeatherRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "AlarmReceiver"

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val remoteDataSource = WeatherRemoteDatasource.getInstance(RetrofitClient.apiService)
        val repo = WeatherRepo.getInstance(
            remoteDataSource, SharedPreferenceImpl.getInstance(context.applicationContext)
        )
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            repo.getCurrentWeather().collect {
                when (it) {
                    is CurrentWeatherState.Success -> {
                        Log.d(TAG, "onReceive: ${it.currentWeather}")
                    }

                    is CurrentWeatherState.Error -> {
                        Log.e(TAG, "onReceive: ${it.message}")
                    }

                    CurrentWeatherState.Loading -> {}
                }
            }
        }
    }

    private fun handleResponse(context: Context) {

    }
}