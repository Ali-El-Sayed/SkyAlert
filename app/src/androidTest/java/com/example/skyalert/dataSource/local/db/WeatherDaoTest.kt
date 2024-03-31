package com.example.skyalert.dataSource.local.db

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.skyalert.util.getEmptyWeatherObj
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.greaterThan
import org.hamcrest.Matchers.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@SmallTest
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class WeatherDaoTest {
    // database instance
    private lateinit var database: WeatherDatabase

    /**
     *  Rule to suspend function calls
     * */
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()


    /**
     * Setup the database
     * */
    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WeatherDatabase::class.java
        ).build()
    }

    @Test
    fun insertWeather_currentWeatherInstance_true() = runTest {
        // GIVEN - Insert a weather
        val weather = getEmptyWeatherObj()
        // WHEN - Insert a weather
        val id = async { database.weatherDao().insertCurrentWeather(weather) }.await()
        // THEN - Verify that the weather is inserted
        assertThat(id, `is`(greaterThan(0L)))
    }

    @Test
    fun getGPSWeather_GpsCurrentWeatherInstance_true() = runTest {
        // GIVEN - Insert a weather
        val weather = getEmptyWeatherObj()
        weather.isGPS = true
        database.weatherDao().insertCurrentWeather(weather)
        // WHEN - Get the weather
        val loaded = async { database.weatherDao().getGPSWeather() }.await()
        // THEN - Verify that the loaded data is correct
        assertThat(loaded.isGPS, `is`(true))
    }

    @Test
    fun getMapWeather_MapCurrentWeatherInstance_true() = runTest {
        // GIVEN - Insert a weather
        val weather = getEmptyWeatherObj()
        weather.isMap = true
        database.weatherDao().insertCurrentWeather(weather)
        // WHEN - Get the weather
        val loaded = async { database.weatherDao().getMapWeather() }.await()
        // THEN - Verify that the loaded data is correct
        assertThat(loaded.isMap, `is`(true))
    }

    @Test
    fun getFavoriteWeather_FavoriteCurrentWeatherInstance_true() = runTest {
        // GIVEN - Insert a weather
        for (i in 1..5) {
            val weather = getEmptyWeatherObj()
            weather.isFavorite = true
            database.weatherDao().insertCurrentWeather(weather)
        }
        // WHEN - Get the weather
        val loaded = async { database.weatherDao().getBookmarks() }.await()
        // THEN - Verify that the loaded data is correct
        for (weather in loaded) {
            assertThat(weather.isFavorite, `is`(true))
        }
    }

    /**
     *  Close the database
     * */
    @After
    fun closeDb() = database.close()

}