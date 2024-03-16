package com.example.skyalert.broadcastreceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.widget.Toast


class LocationBroadcastReceiver(private val onLocationChange: OnLocationChange) :
    BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if (isGpsEnabled || isNetworkEnabled) {
            // GPS is enabled
            Toast.makeText(context, "GPS is enabled", Toast.LENGTH_SHORT).show()
            onLocationChange.onChange()

        } else {
            // GPS is disabled
            Toast.makeText(context, "GPS is disabled", Toast.LENGTH_SHORT).show()
        }
    }
}