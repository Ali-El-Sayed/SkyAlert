package com.example.skyalert.view.screens.home.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.skyalert.R
import com.example.skyalert.databinding.HourlyForecastCardBinding
import com.example.skyalert.model.remote.Day
import com.example.skyalert.network.NetworkHelper
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
        when (position) {
            0 -> {
                binding.root.background = binding.root.resources.getDrawable(
                    R.drawable.hourly_selected_background,
                    binding.root.context.theme
                )
                binding.textViewHour.text = binding.root.resources.getString(R.string.now)
                binding.textViewTemperature.text = "${day.main.temp.toInt()}°"

                Glide.with(binding.root.context).load(NetworkHelper.getIconUrl(day.weather[0].icon))
                    .into(binding.imageViewIcon)
            }

            in 1 until (itemCount - 1) -> {
                val time = java.util.Date(day.dt.toLong() * 1000)
                val hours = SimpleDateFormat("ha").format(time)
                binding.textViewHour.text = hours.uppercase()

                binding.textViewTemperature.text = "${day.main.temp.toInt()}°"

                Glide.with(binding.root.context).load(NetworkHelper.getIconUrl(day.weather[0].icon))
                    .into(binding.imageViewIcon)
            }

            itemCount - 1 -> {
                val time = java.util.Date(day.sys.sunrise.toLong() * 1000)
                val hours = SimpleDateFormat("ha").format(time)
                binding.textViewHour.text = hours.uppercase()
                binding.textViewTemperature.text =
                    binding.root.resources.getString(R.string.sunrise)
                binding.imageViewIcon.setImageResource(R.drawable.ic_sunrise)
            }
        }
    }

    class HourlyForecastViewHolder(binding: HourlyForecastCardBinding) :
        RecyclerView.ViewHolder(binding.root)


}




