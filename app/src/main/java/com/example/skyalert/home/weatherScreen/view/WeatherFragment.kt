package com.example.skyalert.home.weatherScreen.view

import android.Manifest
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.example.example.CurrentWeather
import com.example.skyalert.DataSource.remote.WeatherRemoteDatasource
import com.example.skyalert.R
import com.example.skyalert.broadcastreceiver.LocationBroadcastReceiver
import com.example.skyalert.broadcastreceiver.OnLocationChange
import com.example.skyalert.databinding.FragmentWeatherBinding
import com.example.skyalert.home.weatherScreen.viewModel.WeatherScreenViewModel
import com.example.skyalert.network.RetrofitClient
import com.example.skyalert.network.model.CurrentWeatherState
import com.example.skyalert.repository.WeatherRepo
import com.example.skyalert.util.GPSUtils
import com.example.skyalert.util.PermissionUtils
import com.example.skyalert.util.WeatherViewModelFactory
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.Locale

class WeatherFragment : Fragment(), OnLocationChange {
    private lateinit var binding: FragmentWeatherBinding
    private var longitude: Double = 0.0
    private var latitude: Double = 0.0
    private var address: String = ""
    private val TAG = "WeatherFragment"
    private val DELAY_IN_LOCATION_REQUEST = 20000L
    private lateinit var viewModel: WeatherScreenViewModel
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            super.onLocationResult(p0)
            val location = p0.lastLocation
            location?.let {
                latitude = location.latitude
                longitude = location.longitude
                Log.d(TAG, "Latitude: $latitude")
                Log.d(TAG, "Longitude: $longitude")
                viewModel.getCurrentWeather(latitude, longitude)
//                getLocationFromCoordinates(
//                    requireActivity(), location.latitude, location.longitude
//                )
            }
        }
    }
    private val fusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(requireActivity())
    }
    private val permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
    )
    private lateinit var locationBroadcastReceiver: LocationBroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val remoteDataSource = WeatherRemoteDatasource.getInstance(RetrofitClient.apiService)
        val repo = WeatherRepo.getInstance(remoteDataSource)
        val factory = WeatherViewModelFactory(repo)
        viewModel = ViewModelProvider(this, factory)[WeatherScreenViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentWeatherBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Set up the toolbar
        val navController = findNavController()
        binding.toolbar.setupWithNavController(
            navController, AppBarConfiguration(navController.graph)
        )
        val menuHost:MenuHost = requireActivity()
        menuHost.addMenuProvider(object:MenuProvider{
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.toolbar_weather_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when(menuItem.itemId){
                    R.id.action_search -> {
                        findNavController().navigate(R.id.action_weatherFragment_to_searchFragment)
                    }
                }
                return true
            }

        })


        // Observe the current weather
        lifecycleScope.launch {
            viewModel.currentWeather.collect {
                when (it) {
                    is CurrentWeatherState.Loading -> {
                        binding.currentWeatherProgressBar.visibility = View.VISIBLE
                    }

                    is CurrentWeatherState.Success -> {
                        binding.currentWeatherProgressBar.visibility = View.GONE
                        val currentWeather = it.currentWeather
                        updateUI(currentWeather)
                        Log.d(TAG, "Current Weather: $currentWeather")

                    }

                    is CurrentWeatherState.Error -> {
                        binding.currentWeatherProgressBar.visibility = View.GONE
                        Toast.makeText(requireActivity(), "Error: ${it.message}", Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }
        }


    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onStart() {
        super.onStart()
        // Register the broadcast receiver
        locationBroadcastReceiver = LocationBroadcastReceiver(this)
        val intentFilter = IntentFilter("android.location.PROVIDERS_CHANGED")


        requireActivity().registerReceiver(
            locationBroadcastReceiver,
            intentFilter
        )
        // Get the current location
        getLocation()

        requireActivity().registerReceiver(
            locationBroadcastReceiver,
            IntentFilter("android.location.PROVIDERS_CHANGED")
        )
    }


    override fun onStop() {
        super.onStop()
        requireActivity().unregisterReceiver(locationBroadcastReceiver)
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    private fun updateUI(currentWeather: CurrentWeather) {
        binding.cityNameTextView.text = currentWeather.name
        binding.weatherTempTextView.text = "${currentWeather.main.temp.toInt()}"

        val animator = ValueAnimator.ofInt(0, currentWeather.main.temp.toInt())
        animator.duration = 1000
        animator.addUpdateListener { valueAnimator ->
            val value = valueAnimator.animatedValue as Int
            binding.weatherTempTextView.text = value.toString()
        }
        animator.start()

        binding.weatherTempMeasurementsTextView.text = "°C"
        binding.weatherDescriptionTextView.text = currentWeather.weather[0].description
        binding.maxMinTempTextView.text =
            "Max: ${currentWeather.main.tempMax.toInt()}°C Min: ${currentWeather.main.tempMin.toInt()}°C"


        Glide.with(requireActivity()).load(
            "https://openweathermap.org/img/wn/${currentWeather.weather[0].icon}.png"
        ).into(binding.weatherImageView)
    }


    @RequiresApi(Build.VERSION_CODES.S)
    private fun requestLocationPermission(permissions: Array<String>) {
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { isGranted ->
            if (isGranted.containsValue(true)) {
                if (!GPSUtils.isLocationEnabled(requireActivity())) enableLocationService()
                getFreshLocation(locationCallback)
            } else {
                Toast.makeText(requireActivity(), "Permission denied", Toast.LENGTH_LONG).show()
            }
        }.launch(permissions)
    }


    private fun enableLocationService() {
        Toast.makeText(requireActivity(), "Please enable location service", Toast.LENGTH_LONG)
            .show()
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }


    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.S)
    private fun getFreshLocation(callback: LocationCallback) {
        Toast.makeText(requireActivity(), "Location is enabled", Toast.LENGTH_LONG).show()
        fusedLocationProviderClient.requestLocationUpdates(
            LocationRequest.Builder(DELAY_IN_LOCATION_REQUEST).apply {
                setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            }.build(), callback, Looper.myLooper()
        )
    }

    fun getLocationFromCoordinates(context: Context, latitude: Double, longitude: Double) {
        val geocoder = Geocoder(context, Locale.getDefault())
        try {
            val addressList = geocoder.getFromLocation(latitude, longitude, 1)
            if (addressList != null && addressList.size > 0) {
                val address = addressList[0]
                val locationName = address.getAddressLine(0)
                this.address = locationName
                Toast.makeText(context, "address: ${address.countryName}", Toast.LENGTH_LONG).show()
                Log.d(TAG, "address: $address")
                Log.d(TAG, "Location: $locationName")
                Log.d(TAG, "Latitude: $latitude")

            }
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("GeoCoder", "Error reverse geocoding coordinates")
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onChange() {
        getLocation()
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun getLocation() {
        if (PermissionUtils.checkPermission(requireActivity())) {
            if (!GPSUtils.isLocationEnabled(requireActivity())) enableLocationService()
            else getFreshLocation(locationCallback)
        } else requestLocationPermission(permissions)
    }

}


