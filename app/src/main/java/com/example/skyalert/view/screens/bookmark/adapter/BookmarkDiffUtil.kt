package com.example.skyalert.view.screens.bookmark.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.skyalert.model.remote.CurrentWeather

class BookmarkDiffUtil : DiffUtil.ItemCallback<CurrentWeather>() {
    override fun areItemsTheSame(oldItem: CurrentWeather, newItem: CurrentWeather): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: CurrentWeather, newItem: CurrentWeather): Boolean {
        return oldItem == newItem
    }
}