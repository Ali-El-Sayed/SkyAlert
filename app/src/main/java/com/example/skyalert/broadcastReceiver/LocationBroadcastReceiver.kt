package com.example.skyalert.broadcastReceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import com.example.skyalert.util.GPSUtils


class LocationBroadcastReceiver(private val onLocationChange: OnLocationChange) :
    BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action.equals(LocationManager.PROVIDERS_CHANGED_ACTION)) {
            if (GPSUtils.isGPSEnabled(context)) onLocationChange.locationEnabled()
            else onLocationChange.locationDisabled()
        }
    }
}