package com.example.skyalert.view.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.example.skyalert.R
import com.example.skyalert.databinding.AlertResultDialogBinding
import com.example.skyalert.model.remote.CurrentWeather
import com.example.skyalert.network.NetworkHelper
import com.example.skyalert.network.UNITS

class AlertResultDialog(val currentWeather: CurrentWeather) : DialogFragment() {
    private val binding by lazy {
        AlertResultDialogBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tempMeasurements = when (currentWeather.unit) {
            UNITS.METRIC -> requireContext().getString(R.string.celsius_measure)
            UNITS.IMPERIAL -> requireContext().getString(R.string.fahrenheit_measure)
            UNITS.STANDARD -> requireContext().getString(R.string.kelvin_measure)
        }

        binding.tvLocationName.text = currentWeather.name
        binding.tvWeatherStatus.text = currentWeather.weather[0].description
        Glide.with(requireContext().applicationContext)
            .load(NetworkHelper.getIconUrl(currentWeather.weather[0].icon))
            .into(binding.ivWeatherIcon)
        val temp =
            "${requireContext().getString(R.string.current_weather)}${currentWeather.main.temp} $tempMeasurements"
        val minTemp =
            "${requireContext().getString(R.string.min)}${currentWeather.main.tempMin} $tempMeasurements"
        val maxTemp =
            "${requireContext().getString(R.string.max)}${currentWeather.main.tempMax} $tempMeasurements"

        val windSpeed = "${requireContext().getString(R.string.wind)}${currentWeather.wind.speed} ${
            requireContext().getString(R.string.m_s)
        }"
        val humidity =
            "${requireContext().getString(R.string.humidity)}${currentWeather.main.humidity}%"
        val pressure =
            "${requireContext().getString(R.string.pressure)}${currentWeather.main.pressure}${
                requireContext().getString(R.string.hpa)
            }"
        binding.tvAlertMessage.text = "$temp\n$maxTemp - $minTemp\n$windSpeed\n$humidity\n$pressure"

        binding.btnOk.setOnClickListener {
            dismiss()
        }
    }

}