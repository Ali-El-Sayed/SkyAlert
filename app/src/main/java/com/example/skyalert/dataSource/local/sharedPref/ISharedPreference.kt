package com.example.skyalert.dataSource.local.sharedPref

import com.example.skyalert.model.remote.Coord
import com.example.skyalert.network.UNITS
import com.example.skyalert.view.screens.settings.model.LOCAL
import com.example.skyalert.view.screens.settings.model.LOCATION_SOURCE

/**
 *  Interface for the shared preference to save and get data from the shared preference
 * */
interface ISharedPreference {

    /**
     * Save the unit to the shared preference
     *   @param unit the unit to be saved
     * */

    fun saveUnit(unit: UNITS)

    /**
     *  Get the unit from the shared preference
     *  @return the unit
     * */
    fun getUnit(): UNITS

    /**
     *  Save the default location to the shared preference
     *  @param coord the location to be saved
     * */
    fun setGPSLocation(coord: Coord)

    /**
     *  Get the default location from the shared preference
     *  @return the location
     * */
    fun getGPSLocation(): Coord

    /**
     * Save the map location to the shared preference
     * @param coord the location to be saved
     * */
    fun setMapLocation(coord: Coord)

    /**
     * Get the map location from the shared preference
     * @return the location
     * */
    fun getMapLocation(): Coord

    /**
     *  Save the location type to the shared preference
     *  @param locationType the location type to be saved
     * */
    fun setLocationSource(locationType: LOCATION_SOURCE)

    /**
     * Get the location type from the shared preference
     * @return the location type
     * */
    fun getLocationSource(): LOCATION_SOURCE

    /**
     *  Save the alert location to the shared preference
     *  @param coord the location to be saved
     * */

    fun saveAlertCoord(coord: Coord)

    /**
     *  Get the alert location from the shared preference
     *  @return the location
     * */
    fun getAlertCoord(): Coord

    /**
     *  Save the language to the shared preference
     *  @param language the language to be saved
     * */
    fun setLanguage(language: LOCAL)

    /**
     *  Get the language from the shared preference
     *  @return the language
     * */
    fun getLanguage(): LOCAL

    fun getFiveDaysForecastFileName(): String
    fun saveFiveDaysForecastFileName(fileName: String)
}