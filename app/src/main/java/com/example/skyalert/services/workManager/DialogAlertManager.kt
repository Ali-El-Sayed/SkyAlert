package com.example.skyalert.services.workManager

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.example.skyalert.dataSource.local.WeatherLocalDatasourceImpl
import com.example.skyalert.dataSource.local.db.WeatherDatabase
import com.example.skyalert.dataSource.local.sharedPref.SharedPreferenceImpl
import com.example.skyalert.dataSource.remote.WeatherRemoteDatasource
import com.example.skyalert.model.remote.Coord
import com.example.skyalert.network.RetrofitClient
import com.example.skyalert.network.model.CurrentWeatherState
import com.example.skyalert.repository.WeatherRepo
import com.example.skyalert.view.screens.map.ALERT_RESULT_CONSTANTS.CURRENT_WEATHER
import com.example.skyalert.view.screens.map.MAP_CONSTANTS
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

const val TAG = "DialogAlertManager"

class DialogAlertManager(
    appContext: Context, params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    private val repo by lazy {
        val remoteDataSource = WeatherRemoteDatasource.getInstance(RetrofitClient.apiService)
        val dao = WeatherDatabase.getInstance(appContext.applicationContext).weatherDao()
        val sharedPref = SharedPreferenceImpl.getInstance(appContext.applicationContext)
        val localDatasource = WeatherLocalDatasourceImpl.WeatherLocalDatasourceImpl.getInstance(
            dao, sharedPref
        )
        WeatherRepo.getInstance(
            remoteDataSource, localDatasource
        )
    }

    override suspend fun doWork(): Result {
        val lat = inputData.getDouble(MAP_CONSTANTS.MAP_LAT, 0.0)
        val lon = inputData.getDouble(MAP_CONSTANTS.MAP_LON, 0.0)
        val data: Data.Builder = Data.Builder()
        repo.getCurrentWeatherByCoord(Coord(lat, lon)).collect {
            when (it) {
                is CurrentWeatherState.Success -> {
                    withContext(Dispatchers.Main) {
                        data.apply {
                            Log.d(TAG, "doWork: ${it.currentWeather}")
                            putString(CURRENT_WEATHER, Gson().toJson(it.currentWeather))
                        }
                    }
                }

                else -> {}
            }
        }

        return Result.success(data.build())
    }
}