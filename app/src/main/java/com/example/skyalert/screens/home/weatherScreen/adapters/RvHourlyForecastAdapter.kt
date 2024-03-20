package com.example.skyalert.screens.home.weatherScreen.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.skyalert.R
import com.example.skyalert.databinding.HourlyForecastCardBinding
import com.example.skyalert.model.Day
import java.text.SimpleDateFormat

class RvHourlyForecastAdapter() :
    ListAdapter<Day, RvHourlyForecastAdapter.HourlyForecastViewHolder>(HourlyForecastDiffUtil()) {
    private lateinit var binding: HourlyForecastCardBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyForecastViewHolder {
        val inflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = HourlyForecastCardBinding.inflate(inflater, parent, false)
        return HourlyForecastViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HourlyForecastViewHolder, position: Int) {
        val day = getItem(position)
        if (position == 0) {
            binding.textViewHour.text = binding.root.resources.getString(R.string.now)
            binding.textViewTemperature.text = "${day.main.temp.toInt()}°"

            Glide.with(binding.root.context)
                .load("https://openweathermap.org/img/wn/${day.weather[0].icon}.png")
                .into(binding.imageViewIcon)
        } else if (position in 1 until 7) {
            val time = java.util.Date(day.dt.toLong() * 1000)
            val hours = SimpleDateFormat("ha").format(time)
            binding.textViewHour.text = hours.uppercase()

            binding.textViewTemperature.text = "${day.main.temp.toInt()}°"

            Glide.with(binding.root.context)
                .load("https://openweathermap.org/img/wn/${day.weather[0].icon}.png")
                .into(binding.imageViewIcon)
        } else if (position == 7) {
            val time = java.util.Date(day.sys.sunrise.toLong() * 1000)
            val hours = SimpleDateFormat("ha").format(time)
            binding.textViewHour.text = hours.uppercase()
            binding.textViewTemperature.text = binding.root.resources.getString(R.string.sunrise)
            binding.imageViewIcon.setImageResource(R.drawable.ic_sunrise)
        }
    }

    class HourlyForecastViewHolder(binding: HourlyForecastCardBinding) :
        RecyclerView.ViewHolder(binding.root)


}




