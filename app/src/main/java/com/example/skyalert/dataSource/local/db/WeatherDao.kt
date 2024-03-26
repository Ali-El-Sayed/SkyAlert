package com.example.skyalert.dataSource.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.skyalert.model.CurrentWeather

@Dao
interface WeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrentWeather(currentWeather: CurrentWeather): Long

    @Query("SELECT * FROM current_weather WHERE isCurrent = TRUE")
    suspend fun getCurrentWeather(): CurrentWeather

}