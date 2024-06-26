package com.example.skyalert.view.screens.home.adapters

import androidx.recyclerview.widget.DiffUtil
import com.example.skyalert.model.remote.Day

class HourlyForecastDiffUtil : DiffUtil.ItemCallback<Day>() {
    override fun areItemsTheSame(oldItem: Day, newItem: Day) = oldItem == newItem

    override fun areContentsTheSame(oldItem: Day, newItem: Day) = oldItem == newItem

}