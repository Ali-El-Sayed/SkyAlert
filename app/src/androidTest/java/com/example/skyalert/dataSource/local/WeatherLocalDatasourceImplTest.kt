package com.example.skyalert.dataSource.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.skyalert.dataSource.local.db.WeatherDatabase
import com.example.skyalert.network.model.CurrentWeatherState
import com.example.skyalert.util.getEmptyWeatherObj
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.greaterThan
import org.hamcrest.Matchers.`is`
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@MediumTest
class WeatherLocalDatasourceImplTest {
    private lateinit var localDatasource: IWeatherLocalDatasource
    private lateinit var database: WeatherDatabase

    // Rule to Execute Synchronous using Architecture Components
    @JvmField
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()


    // Setup the database
    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(), WeatherDatabase::class.java
        ).allowMainThreadQueries().build()
        localDatasource = WeatherLocalDatasourceImpl.WeatherLocalDatasourceImpl.getInstance(
            database.weatherDao(), FakeSharedPreference()
        )
    }

    @Test
    fun insertCurrentWeather_currentWeatherInstance_true() = runTest {
        // Given - Insert a weather
        val weather = getEmptyWeatherObj()
        // When - Insert a weather
        val id = async { localDatasource.insertCurrentWeather(weather) }.await()
        // Then - Verify that the weather is inserted
        assertThat(id, `is`(greaterThan(0L)))
    }

    @Test
    fun getGPSWeather_GpsCurrentWeatherInstance_true() = runTest {
        // Given - Insert a weather
        val weather = getEmptyWeatherObj()
        weather.isGPS = true
        val id = async { localDatasource.insertCurrentWeather(weather) }.await()
        // When - Insert a weather
        val currentWeather =
            async { localDatasource.getGPSWeather() }.await() as CurrentWeatherState.Success
        // Then - Verify that the weather is inserted
        assertThat(currentWeather.currentWeather.idRoom, `is`(id))
        assertThat(currentWeather.currentWeather.isGPS, `is`(true))
    }


    @Test
    fun getMapWeather_MapCurrentWeatherInstance_true() = runTest {
        // Given - Insert a weather
        val weather = getEmptyWeatherObj()
        weather.isMap = true
        val id = async { localDatasource.insertCurrentWeather(weather) }.await()
        // When - Insert a weather
        val currentWeather =
            async { localDatasource.getMapWeather() }.await() as CurrentWeatherState.Success
        // Then - Verify that the weather is inserted
        assertThat(currentWeather.currentWeather.idRoom, `is`(id))
        assertThat(currentWeather.currentWeather.isMap, `is`(true))
    }

    @Test
    fun getFavoriteWeather_FavoriteCurrentWeatherInstance_true() = runTest {
        // Given - Insert a weather
        for (i in 1..3) {
            val weather = getEmptyWeatherObj()
            weather.isFavorite = true
            async { localDatasource.insertCurrentWeather(weather) }.await()
        }
        // When - Insert a weather
        localDatasource.getFavoriteWeather().collect { favoriteWeather ->
            // Then - Verify that the weather is inserted
            for (weather in favoriteWeather) {
                assertThat(weather.isFavorite, `is`(true))
            }
        }

    }


}