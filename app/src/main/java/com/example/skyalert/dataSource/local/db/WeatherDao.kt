package com.example.skyalert.dataSource.local.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.skyalert.model.remote.CurrentWeather
import com.example.skyalert.services.alarm.model.Alert

@Dao
interface WeatherDao {


    // weather methods
    /**
     *  Insert a current weather into the database
     *  @param currentWeather the current weather to be inserted
     * */

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrentWeather(currentWeather: CurrentWeather): Long

    @Update()
    suspend fun updateCurrentWeather(currentWeather: CurrentWeather): Int

    /**
     * Get Current Weather
     * @return the current weather
     * */

    @Query("SELECT * FROM current_weather where isCurrent = 1")
    suspend fun getLocalCurrentWeather(): CurrentWeather

    /**
     *  Get the cached current GPS weather
     *  @return the current weather
     * */
    @Query("SELECT * FROM current_weather WHERE isGPS = TRUE")
    suspend fun getGPSWeather(): CurrentWeather

    /**
     *  Get the cached current map weather
     *  @return the current weather
     * */
    @Query("SELECT * FROM current_weather WHERE isMap = TRUE")
    suspend fun getMapWeather(): CurrentWeather

    /**
     *  Get the cached current favorite weather
     *  @return the current weather
     * */
    @Query("SELECT * FROM current_weather WHERE isFavorite = TRUE")
    suspend fun getBookmarks(): List<CurrentWeather>

    /**
     *  Delete a Favorite weather from the database
     *  @param id the id of the favorite weather to be deleted
     * */
    @Delete
    suspend fun deleteBookmarks(currentWeather: CurrentWeather): Int


    // alert methods
    /**
     *  Get All Scheduled Alarms
     * */
    @Query("SELECT * FROM alert_table")
    suspend fun getAllAlarms(): List<Alert>

    /**
     *  Insert an alert into the database
     *  @param alert the alert to be inserted
     * */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: Alert): Long

    /**
     *  Delete an alert from the database
     *  @param alert the alert to be deleted
     * */
    @Delete
    suspend fun deleteAlert(alert: Alert): Int


}