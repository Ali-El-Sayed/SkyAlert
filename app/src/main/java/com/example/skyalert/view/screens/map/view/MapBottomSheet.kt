package com.example.skyalert.view.screens.map.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.example.skyalert.R
import com.example.skyalert.dataSource.local.sharedPref.SharedPreferenceImpl
import com.example.skyalert.dataSource.remote.WeatherRemoteDatasource
import com.example.skyalert.databinding.FragmentMapBottomSheetBinding
import com.example.skyalert.model.Coord
import com.example.skyalert.network.NetworkHelper
import com.example.skyalert.network.RetrofitClient
import com.example.skyalert.network.model.CurrentWeatherState
import com.example.skyalert.repository.WeatherRepo
import com.example.skyalert.util.WeatherViewModelFactory
import com.example.skyalert.view.screens.map.viewModel.MapViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch

class MapBottomSheet(private val coord: Coord) : BottomSheetDialogFragment() {
    private val binding by lazy {
        FragmentMapBottomSheetBinding.inflate(layoutInflater, null, false)
    }

    private val viewModel: MapViewModel by lazy {
        val remoteDataSource = WeatherRemoteDatasource.getInstance(RetrofitClient.apiService)
        val repo = WeatherRepo.getInstance(
            remoteDataSource, SharedPreferenceImpl.getInstance(requireActivity().applicationContext)
        )
        val factory = WeatherViewModelFactory(repo)
        ViewModelProvider(this, factory)[MapViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = binding.root


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getCurrentWeatherByCoord(coord)

        binding.btnSetAsDefaultLocation.setOnClickListener {
            viewModel.setMapLocation(coord)
            Toast.makeText(requireContext(), "Map location updated", Toast.LENGTH_SHORT).show()
            it.isEnabled = false
        }

        /**
         *  Retrieve the current weather data and update the UI
         * */
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.currentWeather.collect {
                    when (it) {
                        is CurrentWeatherState.Loading -> {
                            Toast.makeText(requireContext(), "Loading", Toast.LENGTH_SHORT).show()
                        }

                        is CurrentWeatherState.Success -> {
                            binding.textViewLocation.text = it.currentWeather.name
                            binding.textViewWeatherDescription.text =
                                it.currentWeather.weather[0].description

                            val lon = resources.getString(R.string.longitude)
                            val lat = resources.getString(R.string.latitude)
                            binding.textViewLongitude.text = "$lon: ${it.currentWeather.coord.lon}"
                            binding.textViewLatitude.text = "$lat: ${it.currentWeather.coord.lat}"

                            Glide.with(requireActivity())
                                .load(NetworkHelper.getIconUrl(it.currentWeather.weather[0].icon))
                                .into(binding.imageViewWeatherIcon)
                        }

                        is CurrentWeatherState.Error -> {
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        }
                    }

                }
            }
        }
    }
}