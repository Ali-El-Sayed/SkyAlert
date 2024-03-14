package com.example.skyalert.home.weatherScreen.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.skyalert.DataSource.remote.WeatherRemoteDatasource
import com.example.skyalert.databinding.FragmentWeatherBinding
import com.example.skyalert.home.weatherScreen.viewModel.WeatherScreenViewModel
import com.example.skyalert.network.RetrofitClient
import com.example.skyalert.network.model.CurrentWeatherState
import com.example.skyalert.repository.WeatherRepo
import com.example.skyalert.util.WeatherViewModelFactory
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.Locale

class WeatherFragment : Fragment() {
    private lateinit var binding: FragmentWeatherBinding
    private var longitude: Double = 0.0
    private var latitude: Double = 0.0
    private var address: String = ""
    private val TAG = "WeatherFragment"
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
        LocationServices.getFusedLocationProviderClient(
            requireActivity()
        )
    }
    private val permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
    )

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val remoteDataSource = WeatherRemoteDatasource.getInstance(RetrofitClient.apiService)
        val repo = WeatherRepo.getInstance(remoteDataSource)
        val factory = WeatherViewModelFactory(repo)
        viewModel = ViewModelProvider(this, factory)[WeatherScreenViewModel::class.java]

        if (checkPermission()) {
            if (!isLocationEnabled()) enableLocationService()
        } else requestLocationPermission(permissions)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentWeatherBinding.inflate(layoutInflater)
        val navController = findNavController()
        binding.toolbar.setupWithNavController(
            navController, AppBarConfiguration(navController.graph)
        )
        binding

        lifecycleScope.launch {
            viewModel.currentWeather.collect {
                when (it) {
                    is CurrentWeatherState.Loading -> {
                        binding.currentWeatherProgressBar.visibility = View.VISIBLE
                    }

                    is CurrentWeatherState.Success -> {
                        binding.currentWeatherProgressBar.visibility = View.GONE
                        val currentWeather = it.currentWeather
                        Log.d(TAG, "Current Weather: $currentWeather")
                        Toast.makeText(
                            requireActivity(),
                            "Current Weather: ${currentWeather.name}",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    is CurrentWeatherState.Error -> {
                        Toast.makeText(requireActivity(), "Error: ${it.message}", Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }
        }

        return binding.root
    }


    override fun onDestroy() {
        super.onDestroy()
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }


    @RequiresApi(Build.VERSION_CODES.S)
    private fun requestLocationPermission(permissions: Array<String>) {
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { isGranted ->
            if (isGranted.containsValue(true)) {
                if (!isLocationEnabled()) enableLocationService()
                getFreshLocation(locationCallback)
            } else {
                Toast.makeText(requireActivity(), "Permission denied", Toast.LENGTH_LONG).show()
            }
        }.launch(permissions)
    }

    private fun checkPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
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
            LocationRequest.Builder(3000).apply {
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

}


