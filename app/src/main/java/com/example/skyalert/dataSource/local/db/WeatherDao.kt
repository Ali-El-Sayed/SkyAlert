package com.example.skyalert.dataSource.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.skyalert.model.remote.CurrentWeather

@Dao
interface WeatherDao {

    /**
     *  Insert a current weather into the database
     *  @param currentWeather the current weather to be inserted
     * */

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrentWeather(currentWeather: CurrentWeather): Long

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
    suspend fun getFavoriteWeather(): List<CurrentWeather>


}