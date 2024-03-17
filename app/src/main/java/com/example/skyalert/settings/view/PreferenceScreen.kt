package com.example.skyalert.settings.view

import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import com.example.skyalert.R
import com.example.skyalert.dataSource.local.sharedPref.SharedPreferenceImpl
import com.example.skyalert.dataSource.remote.WeatherRemoteDatasource
import com.example.skyalert.network.RetrofitClient
import com.example.skyalert.network.UNITS
import com.example.skyalert.repository.WeatherRepo
import com.example.skyalert.settings.viewModel.SettingsViewModel
import com.example.skyalert.util.WeatherViewModelFactory

class PreferenceScreen : PreferenceFragmentCompat() {
    private lateinit var viewModel: SettingsViewModel
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        val remoteDataSource = WeatherRemoteDatasource.getInstance(RetrofitClient.apiService)
        val repo = WeatherRepo.getInstance(
            remoteDataSource,
            SharedPreferenceImpl.getInstance(requireActivity().applicationContext)
        )
        val factory = WeatherViewModelFactory(repo)
        viewModel = ViewModelProvider(this, factory)[SettingsViewModel::class.java]

        setupUnitsPref()
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
            Toast.makeText(context, "Unit: $unit", Toast.LENGTH_SHORT).show()
            val unitSummary = when (unit.lowercase()) {
                getString(R.string.metric).lowercase() -> getString(R.string.celsius_m_s)
                getString(R.string.imperial).lowercase() -> getString(R.string.fahrenheit_mph)
                getString(R.string.standard).lowercase() -> getString(R.string.kelvin_m_s)
                else -> getString(R.string.celsius_m_s)
            }
            viewModel.setUnit(UNITS.valueOf(unit.uppercase()))
            preference.summary = "${getString(R.string.units)}: $unitSummary"
            true
        }
    }


}