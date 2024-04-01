package com.example.skyalert.services.broadcastReceiver.locationReceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import com.example.skyalert.util.GPS_NETWORK_Utils


class LocationBroadcastReceiver(private val onLocationChange: OnLocationChange) :
    BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action.equals(LocationManager.PROVIDERS_CHANGED_ACTION)) {
            if (GPS_NETWORK_Utils.isGPSEnabled(context)) onLocationChange.locationEnabled()
            else onLocationChange.locationDisabled()
        }
    }
}