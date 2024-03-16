package com.example.skyalert.util

import android.content.Context
import android.location.LocationManager


class GPSUtils(private val context: Context) {
    companion object {
        fun isLocationEnabled(context: Context): Boolean {
            val locationManager =
                context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val isNetworkEnabled =
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            return isGpsEnabled || isNetworkEnabled
        }
    }
}