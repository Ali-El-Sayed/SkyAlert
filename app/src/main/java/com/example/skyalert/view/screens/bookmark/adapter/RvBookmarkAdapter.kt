package com.example.skyalert.view.screens.bookmark.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.skyalert.R
import com.example.skyalert.databinding.BookmarkCardBinding
import com.example.skyalert.interfaces.OnBookmarkCardClickListener
import com.example.skyalert.model.remote.CurrentWeather
import com.example.skyalert.network.NetworkHelper
import com.example.skyalert.network.UNITS

class RvBookmarkAdapter(private val onBookmarkCardClickListener: OnBookmarkCardClickListener) :
    ListAdapter<CurrentWeather, RvBookmarkAdapter.BookmarkViewHolder>(BookmarkDiffUtil()) {
    class BookmarkViewHolder(binding: BookmarkCardBinding) : RecyclerView.ViewHolder(binding.root)

    private lateinit var binding: BookmarkCardBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookmarkViewHolder {
        val inflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = BookmarkCardBinding.inflate(inflater, parent, false)
        return BookmarkViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookmarkViewHolder, position: Int) {
        val currentWeather = getItem(position)
        binding.tvCity.text = currentWeather.name
        binding.tvPressure.text = "${currentWeather.main.pressure} ${
            binding.root.context.resources.getString(R.string.hpa)
        }"
        val unit = when (currentWeather.unit) {
            UNITS.METRIC -> binding.root.context.resources.getString(R.string.celsius_measure)
            UNITS.IMPERIAL -> binding.root.context.resources.getString(R.string.fahrenheit_measure)
            UNITS.STANDARD -> binding.root.context.resources.getString(R.string.kelvin_measure)
        }
        binding.tvTemperature.text = "${currentWeather.main.feelsLike.toInt()} $unit"

        binding.tvWindSpeed.text = "${currentWeather.wind.speed} ${
            binding.root.context.resources.getString(R.string.m_s)
        }"

        Glide.with(binding.root.context)
            .load(NetworkHelper.getIconUrl(currentWeather.weather[0].icon))
            .into(binding.ivWeatherIcon)

        binding.ivTrash.setOnClickListener {
            onBookmarkCardClickListener.onCardClick(currentWeather)
        }
    }


}