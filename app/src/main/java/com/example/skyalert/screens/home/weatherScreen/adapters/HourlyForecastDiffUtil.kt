package com.example.skyalert.screens.home.weatherScreen.adapters

import androidx.recyclerview.widget.DiffUtil
import com.example.skyalert.model.Day

class HourlyForecastDiffUtil : DiffUtil.ItemCallback<Day>() {
    override fun areItemsTheSame(oldItem: Day, newItem: Day) = oldItem == newItem

    override fun areContentsTheSame(oldItem: Day, newItem: Day) = oldItem == newItem

}