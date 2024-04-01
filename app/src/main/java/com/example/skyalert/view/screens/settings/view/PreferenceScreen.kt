package com.example.skyalert.view.screens.settings.view

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModelProvider
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import com.example.skyalert.R
import com.example.skyalert.dataSource.local.WeatherLocalDatasourceImpl
import com.example.skyalert.dataSource.local.db.WeatherDatabase
import com.example.skyalert.dataSource.local.localStorage.LocalStorage
import com.example.skyalert.dataSource.local.sharedPref.SharedPreferenceImpl
import com.example.skyalert.dataSource.remote.WeatherRemoteDatasource
import com.example.skyalert.network.RetrofitClient
import com.example.skyalert.network.UNITS
import com.example.skyalert.repository.WeatherRepo
import com.example.skyalert.util.WeatherViewModelFactory
import com.example.skyalert.util.toCapitalizedWords
import com.example.skyalert.view.screens.settings.model.KEYS
import com.example.skyalert.view.screens.settings.model.LOCAL
import com.example.skyalert.view.screens.settings.model.LOCATION_SOURCE
import com.example.skyalert.view.screens.settings.viewModel.SettingsViewModel

class PreferenceScreen : PreferenceFragmentCompat() {
    private val viewModel: SettingsViewModel by lazy {
        val remoteDataSource = WeatherRemoteDatasource.getInstance(RetrofitClient.apiService)
        val dao = WeatherDatabase.getInstance(requireContext().applicationContext).weatherDao()
        val sharedPref = SharedPreferenceImpl.getInstance(requireActivity().applicationContext)
        val localStorage = LocalStorage.getInstance(requireActivity().applicationContext)
        val localDatasource = WeatherLocalDatasourceImpl.getInstance(
            dao, sharedPref, localStorage
        )
        val repo = WeatherRepo.getInstance(
            remoteDataSource, localDatasource
        )
        val factory = WeatherViewModelFactory(repo)
        ViewModelProvider(this, factory)[SettingsViewModel::class.java]
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        setupUnitsPref()
        setupLocationPref()
        setupLanguagePref()
    }


    private fun setupUnitsPref() {
        findPreference<ListPreference>(KEYS.UNITS.key)?.summary =
            findPreference<ListPreference>(KEYS.UNITS.key)?.value?.let {
                val unitSummary = when (it.lowercase()) {
                    getString(R.string.metric).lowercase() -> getString(R.string.celsius_m_s)
                    getString(R.string.imperial).lowercase() -> getString(R.string.fahrenheit_mph)
                    getString(R.string.standard).lowercase() -> getString(R.string.kelvin_m_s)
                    else -> getString(R.string.celsius_m_s)
                }
                "${getString(R.string.units)}: $unitSummary"
            }

        findPreference<ListPreference>(KEYS.UNITS.key)?.setOnPreferenceChangeListener { preference, newValue ->
            val unit = newValue as String
            val unitSummary = when (unit.lowercase()) {
                getString(R.string.metric).lowercase() -> getString(R.string.celsius_m_s)
                getString(R.string.imperial).lowercase() -> getString(R.string.fahrenheit_mph)
                getString(R.string.standard).lowercase() -> getString(R.string.kelvin_m_s)
                else -> getString(R.string.celsius_m_s)
            }
            viewModel.setUnit(UNITS.valueOf(unit.uppercase()))
            Toast.makeText(context, "${getString(R.string.units)}: $unit", Toast.LENGTH_SHORT).show()
            preference.summary = "${getString(R.string.units)}: $unitSummary"
            true
        }

    }

    private fun setupLocationPref() {
        findPreference<ListPreference>(KEYS.LOCATION_SOURCE.key)?.summary =
            findPreference<ListPreference>(KEYS.LOCATION_SOURCE.key)?.value?.let {
                val sourceSummary = when (it.lowercase()) {
                    getString(R.string.gps).lowercase() -> LOCATION_SOURCE.GPS
                    getString(R.string.map).lowercase() -> LOCATION_SOURCE.MAP
                    else -> LOCATION_SOURCE.GPS
                }
                "${getString(R.string.location_source)}: ${sourceSummary.value.toCapitalizedWords()}"
            }


        findPreference<ListPreference>(KEYS.LOCATION_SOURCE.key)?.setOnPreferenceChangeListener { preference, newValue ->
            val locationSource = newValue as String
            val sourceSummary = when (locationSource.lowercase()) {
                getString(R.string.gps).lowercase() -> LOCATION_SOURCE.GPS
                getString(R.string.map).lowercase() -> LOCATION_SOURCE.MAP
                else -> LOCATION_SOURCE.GPS
            }

            viewModel.setLocationType(sourceSummary)
            Toast.makeText(context, "${getString(R.string.location_source)}: $locationSource", Toast.LENGTH_SHORT).show()
            preference.summary =
                "${getString(R.string.location_source)}: ${sourceSummary.value.toCapitalizedWords()}"
            true
        }
    }

    private fun setupLanguagePref() {
        findPreference<ListPreference>(KEYS.LANGUAGE.key)?.summary =
            findPreference<ListPreference>("language")?.value?.let {
                val languageSummary = when (it.lowercase()) {
                    getString(R.string.en) -> getString(R.string.english)
                    getString(R.string.ar) -> getString(R.string.arabic)
                    else -> getString(R.string.english)
                }
                "${getString(R.string.language)}: $languageSummary"
            }

        findPreference<ListPreference>("language")?.setOnPreferenceChangeListener { preference, newValue ->
            val language = newValue as String
            val languageSummary = when (language) {
                getString(R.string.en) -> getString(R.string.english)
                getString(R.string.ar) -> getString(R.string.arabic)
                else -> getString(R.string.english)
            }
            val local = when (language) {
                getString(R.string.en) -> LOCAL.EN
                getString(R.string.ar) -> LOCAL.AR
                else -> LOCAL.EN
            }
            setLocale(newValue)
            viewModel.setLanguage(local)
            Toast.makeText(context, "${getString(R.string.language)}: $language", Toast.LENGTH_SHORT).show()
            preference.summary = "${getString(R.string.language)}: $languageSummary"
            true
        }
    }


    private fun setLocale(lang: String) {
        AppCompatDelegate.setApplicationLocales(
            LocaleListCompat.forLanguageTags(lang)
        )
    }
}