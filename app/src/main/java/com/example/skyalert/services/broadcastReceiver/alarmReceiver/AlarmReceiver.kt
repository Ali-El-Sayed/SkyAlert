package com.example.skyalert.services.broadcastReceiver.alarmReceiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.bumptech.glide.Glide
import com.example.skyalert.R
import com.example.skyalert.dataSource.local.sharedPref.SharedPreferenceImpl
import com.example.skyalert.dataSource.remote.WeatherRemoteDatasource
import com.example.skyalert.model.CurrentWeather
import com.example.skyalert.network.NetworkHelper.getIconUrl
import com.example.skyalert.network.RetrofitClient
import com.example.skyalert.network.UNITS
import com.example.skyalert.network.model.CurrentWeatherState
import com.example.skyalert.repository.WeatherRepo
import com.example.skyalert.services.notification.NotificationHelper
import com.google.android.gms.location.Priority
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "AlarmReceiver"

class AlarmReceiver() : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val remoteDataSource = WeatherRemoteDatasource.getInstance(RetrofitClient.apiService)
        val repo = WeatherRepo.getInstance(
            remoteDataSource, SharedPreferenceImpl.getInstance(context.applicationContext)
        )
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            repo.getCurrentWeather(repo.getAlertLocation()).collect {
                when (it) {
                    is CurrentWeatherState.Success -> {
                        handleSuccess(context, it.currentWeather)
                    }

                    is CurrentWeatherState.Error -> {
                        Log.e(TAG, "onReceive: ${it.message}")
                    }

                    CurrentWeatherState.Loading -> {}
                }
            }
        }


    }

    private fun handleSuccess(context: Context, currentWeather: CurrentWeather) {
        val title = currentWeather.name
        val tempMeasurements = when (currentWeather.unit) {
            UNITS.METRIC -> context.getString(R.string.celsius_measure)
            UNITS.IMPERIAL -> context.getString(R.string.fahrenheit_measure)
            UNITS.STANDARD -> context.getString(R.string.kelvin_measure)
        }
        val description =
            "${currentWeather.weather[0].description}\n${currentWeather.main.temp}$tempMeasurements"
        val details =
            "\n${context.getString(R.string.humidity)} ${currentWeather.main.humidity}%\n${
                context.getString(R.string.max)
            } ${currentWeather.main.tempMax}$tempMeasurements ${context.getString(R.string.min)} ${currentWeather.main.tempMin}$tempMeasurements"

        val bitmap =
            Glide.with(context).asBitmap().load(getIconUrl(currentWeather.weather[0].icon)).submit()
                .get()

        NotificationHelper.createNotificationChannel(
            context,
            NotificationHelper.ALERT_CHANNEL_ID,
            NotificationHelper.ALERT_CHANNEL_NAME,
            NotificationHelper.WEATHER_CHANNEL_DESCRIPTION,
            NotificationManager.IMPORTANCE_HIGH
        )

        NotificationHelper.createNotification(
            context,
            title,
            description,
            details,
            NotificationHelper.ALERT_CHANNEL_ID,
            bitmap,
            Priority.PRIORITY_HIGH_ACCURACY
        )
    }
}
