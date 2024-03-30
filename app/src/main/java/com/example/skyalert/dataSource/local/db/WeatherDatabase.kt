package com.example.skyalert.dataSource.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.skyalert.model.remote.CurrentWeather
import com.example.skyalert.model.typeConverter.WeatherTypeConverter

@Database(entities = [CurrentWeather::class], version = 1)
@TypeConverters(WeatherTypeConverter::class)
abstract class WeatherDatabase : RoomDatabase() {
    companion object {
        private const val DATABASE_NAME = "weather_db"
        private var _INSTANCE: WeatherDatabase? = null
        fun getInstance(context: Context) = _INSTANCE ?: synchronized(this) {
            _INSTANCE ?: buildDatabase(context).also { _INSTANCE = it }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context, WeatherDatabase::class.java, DATABASE_NAME).build()
    }

    abstract fun weatherDao(): WeatherDao

}