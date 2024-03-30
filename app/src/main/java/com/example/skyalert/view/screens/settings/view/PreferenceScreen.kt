package com.example.skyalert.view.screens.settings.view

import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import com.example.skyalert.R
import com.example.skyalert.dataSource.local.WeatherLocalDatasourceImpl
import com.example.skyalert.dataSource.local.db.WeatherDatabase
import com.example.skyalert.dataSource.local.sharedPref.SharedPreferenceImpl
import com.example.skyalert.dataSource.remote.WeatherRemoteDatasource
import com.example.skyalert.network.RetrofitClient
import com.example.skyalert.network.UNITS
import com.example.skyalert.repository.WeatherRepo
import com.example.skyalert.util.WeatherViewModelFactory
import com.example.skyalert.util.toCapitalizedWords
import com.example.skyalert.view.screens.settings.model.LOCATION_SOURCE
import com.example.skyalert.view.screens.settings.viewModel.SettingsViewModel

class PreferenceScreen : PreferenceFragmentCompat() {
    private lateinit var viewModel: SettingsViewModel
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        val remoteDataSource = WeatherRemoteDatasource.getInstance(RetrofitClient.apiService)
        val dao = WeatherDatabase.getInstance(requireContext().applicationContext).weatherDao()
        val sharedPref = SharedPreferenceImpl.getInstance(requireActivity().applicationContext)
        val localDatasource = WeatherLocalDatasourceImpl.WeatherLocalDatasourceImpl.getInstance(
            dao, sharedPref
        )
        val repo = WeatherRepo.getInstance(
            remoteDataSource, localDatasource
        )
        val factory = WeatherViewModelFactory(repo)
        viewModel = ViewModelProvider(this, factory)[SettingsViewModel::class.java]

        setupUnitsPref()
        setupLocationPref()
    }

    private fun setupUnitsPref() {
        findPreference<ListPreference>("unit")?.summary =
            findPreference<ListPreference>("unit")?.value?.let {
                val unitSummary = when (it.lowercase()) {
                    getString(R.string.metric).lowercase() -> getString(R.string.celsius_m_s)
                    getString(R.string.imperial).lowercase() -> getString(R.string.fahrenheit_mph)
                    getString(R.string.standard).lowercase() -> getString(R.string.kelvin_m_s)
                    else -> getString(R.string.celsius_m_s)
                }
                "${getString(R.string.units)}: $unitSummary"
            }

        findPreference<ListPreference>("unit")?.setOnPreferenceChangeListener { preference, newValue ->
            val unit = newValue as String
            val unitSummary = when (unit.lowercase()) {
                getString(R.string.metric).lowercase() -> getString(R.string.celsius_m_s)
                getString(R.string.imperial).lowercase() -> getString(R.string.fahrenheit_mph)
                getString(R.string.standard).lowercase() -> getString(R.string.kelvin_m_s)
                else -> getString(R.string.celsius_m_s)
            }
            viewModel.setUnit(UNITS.valueOf(unit.uppercase()))
            Toast.makeText(context, "Unit: $unit", Toast.LENGTH_SHORT).show()
            preference.summary = "${getString(R.string.units)}: $unitSummary"
            true
        }

    }

    fun setupLocationPref() {
        findPreference<ListPreference>("location_source")?.summary =
            findPreference<ListPreference>("location_source")?.value?.let {
                val sourceSummary = when (it.lowercase()) {
                    getString(R.string.gps).lowercase() -> LOCATION_SOURCE.GPS
                    getString(R.string.map).lowercase() -> LOCATION_SOURCE.MAP
                    else -> LOCATION_SOURCE.GPS
                }
                "${getString(R.string.location_source)}: ${sourceSummary.value.toCapitalizedWords()}"
            }


        findPreference<ListPreference>("location_source")?.setOnPreferenceChangeListener { preference, newValue ->
            val locationSource = newValue as String
            val sourceSummary = when (locationSource.lowercase()) {
                getString(R.string.gps).lowercase() -> LOCATION_SOURCE.GPS
                getString(R.string.map).lowercase() -> LOCATION_SOURCE.MAP
                else -> LOCATION_SOURCE.GPS
            }

            viewModel.setLocationType(sourceSummary)
            Toast.makeText(context, "Location Source: $locationSource", Toast.LENGTH_SHORT).show()
            preference.summary =
                "${getString(R.string.location_source)}: ${sourceSummary.value.toCapitalizedWords()}"
            true
        }
    }
}