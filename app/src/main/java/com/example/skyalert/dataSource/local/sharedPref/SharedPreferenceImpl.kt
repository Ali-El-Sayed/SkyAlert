package com.example.skyalert.dataSource.local.sharedPref

import android.content.Context
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.skyalert.network.UNITS

class SharedPreferenceImpl(private val context: Context) : ISharedPreference {
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


    companion object {
        @Volatile
        private lateinit var instance: ISharedPreference
        private const val PREF_NAME = "sky_alert_pref"

        fun getInstance(context: Context): ISharedPreference {
            synchronized(this) {
                if (!Companion::instance.isInitialized) {
                    instance = SharedPreferenceImpl(context)
                }
                return instance
            }
        }
    }

    override fun saveUnit(unit: UNITS) {
        sharedPreferences.edit().putString(KEYS.UNIT, unit.value).apply()
    }

    override fun getUnit(): UNITS {
        return sharedPreferences.getString(KEYS.UNIT, UNITS.METRIC.value)?.let {
            Log.d("SharedPref", "getUnit: $it")
            UNITS.valueOf(it.uppercase())
        } ?: UNITS.METRIC
    }
}