package com.example.skyalert.util

import android.content.Context
import android.location.LocationManager


class GPSUtils(private val context: Context) {
    companion object {
        fun isLocationEnabled(context: Context): Boolean {
            return isGPSEnabled(context) || isNetworkEnabled(context)
        }

        fun isGPSEnabled(context: Context): Boolean {
            val locationManager =
                context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        }

        fun isNetworkEnabled(context: Context): Boolean {
            val locationManager =
                context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        }

    }


}