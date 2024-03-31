package com.example.skyalert.view.screens.map.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.skyalert.dataSource.local.db.model.BookmarkedWeatherState
import com.example.skyalert.model.remote.Coord
import com.example.skyalert.repository.FakeWeatherRepo
import com.example.skyalert.util.getEmptyWeatherObj
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class MapViewModelTest {
    /**
     *  MapViewModel
     *  FakeWeatherRepo
     * */
    private lateinit var repo: FakeWeatherRepo
    private lateinit var mapViewModel: MapViewModel

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    /**
     *  setUp function
     * */
    @Before
    fun setUp() {
        repo = FakeWeatherRepo()
        mapViewModel = MapViewModel(repo)
    }

    @Test
    fun getCurrentWeatherByCoord_returnCurrentWeather() = runTest {
        // GIVEN - A map location
        val weather = getEmptyWeatherObj()
        val coord = Coord(11.0, 12.0)
        weather.coord = coord
        repo.insertCurrentWeather(weather)
        // WHEN - Get the current weather by coord
        mapViewModel.getCurrentWeatherByCoord(coord)
        delay(2000)

        // THEN - Verify the current weather is returned
        val result = mapViewModel.currentWeather.first()
        when (result) {
            is BookmarkedWeatherState.Success ->
                assertThat(result.currentWeather.coord, `is`(coord))

            else -> assert(false)
        }
    }


    @Test
    fun setMapLocation_coord_true() {
        // GIVEN - A map location
        val coord = Coord(11.0, 12.0)
        // WHEN - Set the map location
        mapViewModel.setMapLocation(coord)
        // THEN - Verify the map location is set
        assertThat(repo.getMapLocation(), `is`(coord))
    }

    @Test
    fun getMapLocation_returnMapLocation() {
        // GIVEN - A map location
        val coord = Coord(11.0, 12.0)
        repo.setMapLocation(coord)
        // WHEN - Get the map location
        val mapLocation = mapViewModel.getMapLocation()
        // THEN - Verify the map location is returned
        assertThat(mapLocation, `is`(coord))
    }

    @Test
    fun saveAlertLocation_coord_true() {
        // GIVEN - A map location
        val coord = Coord(11.0, 12.0)
        // WHEN - Save the alert location
        mapViewModel.saveAlertLocation(coord)
        // THEN - Verify the alert location is saved
        assertThat(repo.getAlertLocation(), `is`(coord))
    }

}