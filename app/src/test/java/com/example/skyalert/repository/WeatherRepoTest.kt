package com.example.skyalert.repository

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.skyalert.dataSource.local.FakeWeatherLocalDatasource
import com.example.skyalert.dataSource.local.IWeatherLocalDatasource
import com.example.skyalert.dataSource.remote.FakeWeatherRemoteDatasource
import com.example.skyalert.dataSource.remote.IWeatherRemoteDataSource
import com.example.skyalert.network.UNITS
import com.example.skyalert.network.model.CurrentWeatherState
import com.example.skyalert.util.getEmptyWeatherObj
import com.example.skyalert.view.screens.settings.model.LOCATION_SOURCE
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WeatherRepoTest {
    private lateinit var weatherLocalDatasource: IWeatherLocalDatasource
    private lateinit var weatherRemoteDataSource: IWeatherRemoteDataSource
    private lateinit var weatherRepo: WeatherRepo

    @Before
    fun setUp() {
        weatherLocalDatasource = FakeWeatherLocalDatasource()
        weatherRemoteDataSource = FakeWeatherRemoteDatasource()
        weatherRepo = WeatherRepo.getInstance(weatherRemoteDataSource, weatherLocalDatasource)
    }

    /**
     * Shared preference data source tests
     * */

    @Test
    fun setUnit_newUnit_Success() = runTest {
        // Given
        val unit = UNITS.IMPERIAL
        // When
        weatherRepo.setUnit(unit)
        // Then
        assertThat(weatherRepo.getUnit(), `is`(unit))
    }

    @Test
    fun setGPSLocation_newLocation_Success() = runTest {
        // Given
        val coord = getEmptyWeatherObj().coord
        // When
        weatherRepo.setGPSLocation(coord)
        // Then
        assertThat(weatherRepo.getGPSLocation(), `is`(coord))
    }

    @Test
    fun setLocationSource_newLocationSource_Success() = runTest {
        // Given
        val locationSource = LOCATION_SOURCE.MAP
        // When
        weatherRepo.setLocationSource(locationSource)
        // Then
        assertThat(weatherRepo.getLocationSource(), `is`(locationSource))
    }

    /**
     * Local data source tests
     * */
    @Test
    fun insertCurrentWeather_GPSWeather_Success() = runTest {
        // Given
        weatherRepo.setLocationSource(LOCATION_SOURCE.GPS)
        val currentWeather = getEmptyWeatherObj()
        // When
        val id = weatherRepo.insertCurrentWeather(currentWeather)

        assertThat(id, `is`(not(-1)))
    }

    @Test
    fun insertCurrentWeather_MapWeather_Success() = runTest {
        // Given
        weatherRepo.setLocationSource(LOCATION_SOURCE.MAP)
        val currentWeather = getEmptyWeatherObj()
        // When
        val id = weatherRepo.insertCurrentWeather(currentWeather)

        assertThat(id, `is`(not(-1)))
    }

    @Test
    fun insertCurrentWeather_FavoriteWeather_Success() = runTest {
        // Given
        val currentWeather = getEmptyWeatherObj()
        currentWeather.isFavorite = true
        // When
        val id = weatherRepo.insertCurrentWeather(currentWeather)

        assertThat(id, `is`(not(-1)))
    }

    @Test
    fun getGPSWeather_Success() = runTest {
        // Given
        weatherRepo.setLocationSource(LOCATION_SOURCE.GPS)
        val currentWeather = getEmptyWeatherObj()
        weatherRepo.insertCurrentWeather(currentWeather)
        // When
        val gpsWeather = weatherRepo.getGPSWeather() as CurrentWeatherState.Success

        assertThat(gpsWeather.currentWeather.isGPS, `is`(true))
    }

    @Test
    fun getMapWeather_Success() = runTest {
        // Given
        weatherRepo.setLocationSource(LOCATION_SOURCE.MAP)
        val currentWeather = getEmptyWeatherObj()
        weatherRepo.insertCurrentWeather(currentWeather)
        // When
        val mapWeather = weatherRepo.getMapWeather() as CurrentWeatherState.Success

        assertThat(mapWeather.currentWeather.isMap, `is`(true))
    }

    @Test
    fun getFavoriteWeather_Success() = runTest {
        // Given
        val currentWeather = getEmptyWeatherObj()
        currentWeather.isFavorite = true
        val id = weatherRepo.insertCurrentWeather(currentWeather)
        currentWeather.idRoom = id
        // When
        val favoriteWeather = weatherRepo.getFavoriteWeather().first {
            it.isFavorite
        }
        assertThat(favoriteWeather.isFavorite, `is`(true))
    }


}