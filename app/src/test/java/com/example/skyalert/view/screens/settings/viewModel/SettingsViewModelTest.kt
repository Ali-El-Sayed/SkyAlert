package com.example.skyalert.view.screens.settings.viewModel

import com.example.skyalert.model.remote.Coord
import com.example.skyalert.network.UNITS
import com.example.skyalert.repository.FakeWeatherRepo
import com.example.skyalert.view.screens.settings.model.LOCATION_SOURCE
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.not
import org.hamcrest.Matchers.nullValue
import org.junit.Before
import org.junit.Test

class SettingsViewModelTest {
    private lateinit var iWeatherRepo: FakeWeatherRepo
    private lateinit var settingsViewModel: SettingsViewModel

    @Before
    fun setUp() {
        iWeatherRepo = FakeWeatherRepo()
        settingsViewModel = SettingsViewModel(iWeatherRepo)
    }

    @Test
    fun setUnit_measurementUnit_isTrue() {
        // Given unit
        val unit = UNITS.IMPERIAL
        // When setUnit is called
        settingsViewModel.setUnit(unit)
        // Then the unit is set
        // assert that iWeatherRepo.getUnit() == unit
        assertThat(iWeatherRepo.getUnit(), `is`(unit))
    }

    @Test
    fun getMapLocation_noNull() {
        assertThat(settingsViewModel.getMapLocation(), not(nullValue()))
    }

    @Test
    fun setMapLocation_mapLocation_isTrue() {
        // Given coord
        val coord = Coord(1.0, 1.0)
        // When setMapLocation is called
        settingsViewModel.setMapLocation(coord)
        // Then the map location is set
        assertThat(settingsViewModel.getMapLocation(), `is`(coord))
    }

    @Test
    fun setLocationType_locationType_isTrue() {
        // Given location type
        val locationType = LOCATION_SOURCE.MAP
        // When setLocationType is called
        settingsViewModel.setLocationType(locationType)
        // Then the location type is set
        assertThat(iWeatherRepo.getLocationSource(), `is`(locationType))
    }
}
