package com.example.skyalert.util

import android.content.Context
import android.location.LocationManager
import java.io.IOException


class GPS_NETWORK_Utils(private val context: Context) {
    companion object {
        fun isLocationEnabled(context: Context): Boolean {
            return isGPSEnabled(context) || isNetworkEnabled(context)
        }

        fun isGPSEnabled(context: Context): Boolean {
            val locationManager =
                context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        }

        private fun isNetworkEnabled(context: Context): Boolean {
            val locationManager =
                context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        }

        fun isNetworkConnected(context: Context): Boolean {
            val runtime = Runtime.getRuntime()
            try {
                val ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8")
                val exitValue = ipProcess.waitFor()
                return exitValue == 0
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            return false
        }

    }


}