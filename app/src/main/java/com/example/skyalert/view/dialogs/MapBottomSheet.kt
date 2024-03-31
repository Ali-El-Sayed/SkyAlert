package com.example.skyalert.view.dialogs

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
import com.example.skyalert.dataSource.local.WeatherLocalDatasourceImpl
import com.example.skyalert.dataSource.local.db.WeatherDatabase
import com.example.skyalert.dataSource.local.sharedPref.SharedPreferenceImpl
import com.example.skyalert.dataSource.remote.WeatherRemoteDatasource
import com.example.skyalert.databinding.FragmentMapBottomSheetBinding
import com.example.skyalert.interfaces.BottomSheetCallbacks
import com.example.skyalert.model.remote.Coord
import com.example.skyalert.network.NetworkHelper
import com.example.skyalert.network.RetrofitClient
import com.example.skyalert.network.model.CurrentWeatherState
import com.example.skyalert.repository.WeatherRepo
import com.example.skyalert.util.WeatherViewModelFactory
import com.example.skyalert.view.screens.map.MAP_CONSTANTS
import com.example.skyalert.view.screens.map.viewModel.MapViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MapBottomSheet(private val bottomSheetCallbacks: BottomSheetCallbacks) : BottomSheetDialogFragment() {
    private val binding by lazy {
        FragmentMapBottomSheetBinding.inflate(layoutInflater, null, false)
    }
    private val coord by lazy {
        Coord(
            arguments?.getDouble(MAP_CONSTANTS.MAP_LAT) ?: 0.0, arguments?.getDouble(MAP_CONSTANTS.MAP_LON) ?: 0.0
        )
    }

    private val viewModel: MapViewModel by lazy {
        val remoteDataSource = WeatherRemoteDatasource.getInstance(RetrofitClient.apiService)
        val dao = WeatherDatabase.getInstance(requireContext().applicationContext).weatherDao()
        val sharedPref = SharedPreferenceImpl.getInstance(requireActivity().applicationContext)
        val localDatasource = WeatherLocalDatasourceImpl.WeatherLocalDatasourceImpl.getInstance(
            dao, sharedPref
        )
        val repo = WeatherRepo.getInstance(
            remoteDataSource, localDatasource
        )
        val factory = WeatherViewModelFactory(repo)
        ViewModelProvider(this, factory)[MapViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        viewModel.getCurrentWeatherByCoord(coord)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        /**
         *  Retrieve the current weather data and update the UI
         * */
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.currentWeather.collect {
                    when (it) {

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

                        else -> {}
                    }

                }
            }
        }
        binding.imageViewAlert.setOnClickListener {
            bottomSheetCallbacks.setAlert()
            dismiss()
        }

        binding.btnSetAsDefaultLocation.setOnClickListener {
            bottomSheetCallbacks.setDefaultLocation(coord)
        }

        binding.imageViewBookmark.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                val result =
                    viewModel.addToFavorite((viewModel.currentWeather.value as CurrentWeatherState.Success).currentWeather)
                withContext(Dispatchers.Main) {
                    if (result > 0) Toast.makeText(
                        requireContext(), "Added to bookmark", Toast.LENGTH_SHORT
                    ).show()
                    else Toast.makeText(
                        requireContext(), "Failed to add to bookmark", Toast.LENGTH_SHORT
                    ).show()
                    dismiss()
                }
            }

            /**
             *  Retrieve the current weather data and update the UI
             * */
            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.currentWeather.collect {
                        when (it) {
                            is CurrentWeatherState.Loading -> {
                            }

                            is CurrentWeatherState.Success -> {
                                binding.textViewLocation.text = it.currentWeather.name
                                binding.textViewWeatherDescription.text = it.currentWeather.weather[0].description

                                val lon = resources.getString(R.string.longitude)
                                val lat = resources.getString(R.string.latitude)
                                binding.textViewLongitude.text = "$lon: ${it.currentWeather.coord.lon}"
                                binding.textViewLatitude.text = "$lat: ${it.currentWeather.coord.lat}"

                                Glide.with(requireActivity()).load(NetworkHelper.getIconUrl(it.currentWeather.weather[0].icon))
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
}