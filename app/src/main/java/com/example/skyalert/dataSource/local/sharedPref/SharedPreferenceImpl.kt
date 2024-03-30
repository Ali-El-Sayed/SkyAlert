package com.example.skyalert.dataSource.local.sharedPref

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.skyalert.model.remote.Coord
import com.example.skyalert.network.UNITS
import com.example.skyalert.view.screens.settings.model.LOCATION_SOURCE


/**
 *  Implementation of the ISharedPreference
 *  This class is used to save and get the data from the shared preference
 * */
class SharedPreferenceImpl(private val context: Context) : ISharedPreference {

    // Tag for logging
    private val TAG = "SharedPreferenceImpl"

    /**
     *  EncryptedSharedPreferences is used to store the data in shared preference in encrypted form
     *  It uses MasterKey to encrypt the data
     *  MasterKey is used to encrypt the data in shared preference
     * */
    private val masterKeyAlias by lazy {
        MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()
    }

    private val sharedPreferences by lazy {
        EncryptedSharedPreferences.create(
            context,
            PREF_NAME,
            masterKeyAlias,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }


    /**
     *  Singleton pattern to get the instance of EncryptedSharedPreferences
     * */
    companion object {
        @Volatile
        private lateinit var instance: ISharedPreference
        private const val PREF_NAME = "sky_alert_pref"

        fun getInstance(context: Context): ISharedPreference {
            synchronized(this) {
                if (!Companion::instance.isInitialized) instance = SharedPreferenceImpl(context)
                return instance
            }
        }
    }

    /**
     * Save the unit in shared preference
     *  @param unit: UNITS
     * */
    override fun saveUnit(unit: UNITS) {
        sharedPreferences.edit().putString(KEYS.UNIT, unit.value).apply()
    }

    /**
     * Get the unit from shared preference
     * @return UNITS
     * */
    override fun getUnit(): UNITS {
        return sharedPreferences.getString(KEYS.UNIT, UNITS.METRIC.value)?.let {
            UNITS.valueOf(it.uppercase())
        } ?: UNITS.METRIC
    }

    /**
     *  Save the default location in shared preference
     *  @param coord: Coord
     * */

    override fun setGPSLocation(coord: Coord) {
        sharedPreferences.edit().putString(KEYS.GPS_LOCATION_LAT, coord.lat.toString()).apply()
        sharedPreferences.edit().putString(KEYS.GPS_LOCATION_LON, coord.lon.toString()).apply()
    }

    /**
     * Get the default location from shared preference
     * @return Cord: Coord
     * */

    override fun getGPSLocation(): Coord {
        val lat =
            sharedPreferences.getString(KEYS.GPS_LOCATION_LAT, DEFAULT_LOCATION_LAT.toString())
                ?.toDouble() ?: DEFAULT_LOCATION_LAT
        val lon =
            sharedPreferences.getString(KEYS.GPS_LOCATION_LON, DEFAULT_LOCATION_LON.toString())
                ?.toDouble() ?: DEFAULT_LOCATION_LON
        return Coord(lat, lon)
    }

    override fun setMapLocation(coord: Coord) {
        sharedPreferences.edit().putString(KEYS.MAP_LOCATION_LAT, coord.lat.toString()).apply()
        sharedPreferences.edit().putString(KEYS.MAP_LOCATION_LON, coord.lon.toString()).apply()
    }

    override fun getMapLocation(): Coord {
        val lat =
            sharedPreferences.getString(KEYS.MAP_LOCATION_LAT, DEFAULT_LOCATION_LAT.toString())
                ?.toDouble() ?: DEFAULT_LOCATION_LAT
        val lon =
            sharedPreferences.getString(KEYS.MAP_LOCATION_LON, DEFAULT_LOCATION_LON.toString())
                ?.toDouble() ?: DEFAULT_LOCATION_LON
        return Coord(lat, lon)
    }

    /**
     * Save the location type in shared preference
     * @param locationType: LOCATION_TYPE
     * */
    override fun setLocationSource(locationType: LOCATION_SOURCE) {
        sharedPreferences.edit().putString(KEYS.LOCATION_SOURCE, locationType.value).apply()
    }

    /**
     *  Get the location type from shared preference
     *  @return LOCATION_TYPE
     * */
    override fun getLocationSource() =
        sharedPreferences.getString(KEYS.LOCATION_SOURCE, LOCATION_SOURCE.GPS.value)?.let {
            return LOCATION_SOURCE.valueOf(it.uppercase())
        } ?: LOCATION_SOURCE.GPS

    /**
     *  Save the alert location in shared preference
     *  @param coord: Coord
     * */
    override fun saveAlertCoord(coord: Coord) {
        sharedPreferences.edit().putString(KEYS.ALERT_LOCATION_LAT, coord.lat.toString()).apply()
        sharedPreferences.edit().putString(KEYS.ALERT_LOCATION_LON, coord.lon.toString()).apply()
    }

    /**
     *  Get the alert location from shared preference
     *  @return Coord
     * */
    override fun getAlertCoord(): Coord {
        val lat =
            sharedPreferences.getString(KEYS.ALERT_LOCATION_LAT, DEFAULT_LOCATION_LAT.toString())
                ?.toDouble() ?: DEFAULT_LOCATION_LAT
        val lon =
            sharedPreferences.getString(KEYS.ALERT_LOCATION_LON, DEFAULT_LOCATION_LON.toString())
                ?.toDouble() ?: DEFAULT_LOCATION_LON
        return Coord(lat, lon)
    }

}