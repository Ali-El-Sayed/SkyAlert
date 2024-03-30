package com.example.skyalert.view.screens.home.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.skyalert.R
import com.example.skyalert.databinding.FiveDaysForecastCardBinding
import com.example.skyalert.model.remote.Day
import com.example.skyalert.network.NetworkHelper
import com.example.skyalert.util.toCapitalizedWords
import java.text.SimpleDateFormat
import kotlin.math.roundToInt

class RvFiveDaysForecastAdapter :
    ListAdapter<Day, RvFiveDaysForecastAdapter.FiveDaysForecastViewHolder>(HourlyForecastDiffUtil()) {
    class FiveDaysForecastViewHolder(binding: FiveDaysForecastCardBinding) :
        RecyclerView.ViewHolder(binding.root)

    private lateinit var binding: FiveDaysForecastCardBinding


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FiveDaysForecastViewHolder {
        val inflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = FiveDaysForecastCardBinding.inflate(inflater, parent, false)
        return FiveDaysForecastViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FiveDaysForecastViewHolder, position: Int) {
        val day = getItem(position)
        if (position == 0) binding.textViewDayTitle.text =
            binding.root.resources.getString(R.string.today)
        else {
            val date = java.util.Date(day.dt.toLong() * 1000)
            val dayOfWeek = SimpleDateFormat("EEEE").format(date).substring(0, 3)
            binding.textViewDayTitle.text = dayOfWeek.toCapitalizedWords()
        }


        binding.textViewMinTemp.text = "${day.main.tempMin.roundToInt()}°"
        binding.textViewMaxTemp.text = "${day.main.tempMax.roundToInt()}°"

        binding.progressIndicatorTemperature.max =
            day.main.tempMax.toInt() + day.main.tempMin.toInt()

        binding.progressIndicatorTemperature.progress = day.main.feelsLike.toInt()

        Glide.with(binding.root.context).load(
            NetworkHelper.getIconUrl(day.weather[0].icon)
        ).into(binding.imageViewWeatherIcon)

    }

}