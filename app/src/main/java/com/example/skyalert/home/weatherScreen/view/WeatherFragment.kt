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
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.bumptech.glide.Glide
import com.example.example.CurrentWeather
import com.example.skyalert.R
import com.example.skyalert.broadcastreceiver.LocationBroadcastReceiver
import com.example.skyalert.broadcastreceiver.OnLocationChange
import com.example.skyalert.dataSource.local.sharedPref.SharedPreferenceImpl
import com.example.skyalert.dataSource.remote.WeatherRemoteDatasource
import com.example.skyalert.databinding.FragmentWeatherBinding
import com.example.skyalert.home.weatherScreen.viewModel.WeatherScreenViewModel
import com.example.skyalert.network.RetrofitClient
import com.example.skyalert.network.UNITS
import com.example.skyalert.network.model.CurrentWeatherState
import com.example.skyalert.repository.WeatherRepo
import com.example.skyalert.util.GPSUtils
import com.example.skyalert.util.PermissionUtils
import com.example.skyalert.util.WeatherViewModelFactory
import com.example.skyalert.util.toCapitalizedWords
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.Locale


class WeatherFragment : Fragment(), OnLocationChange {
    private val binding: FragmentWeatherBinding by lazy {
        FragmentWeatherBinding.inflate(layoutInflater)
    }

    private var longitude: Double = 0.0
    private var latitude: Double = 0.0
    private var address: String = ""
    private val TAG = "WeatherFragment"
    private val DELAY_IN_LOCATION_REQUEST = 2000000L
    private val viewModel: WeatherScreenViewModel by lazy {
        val remoteDataSource = WeatherRemoteDatasource.getInstance(RetrofitClient.apiService)
        val repo = WeatherRepo.getInstance(
            remoteDataSource, SharedPreferenceImpl.getInstance(requireActivity().applicationContext)
        )
        val factory = WeatherViewModelFactory(repo)
        ViewModelProvider(this, factory)[WeatherScreenViewModel::class.java]
    }
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

    @RequiresApi(Build.VERSION_CODES.Q)
    private val permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_BACKGROUND_LOCATION
    )
    private lateinit var locationBroadcastReceiver: LocationBroadcastReceiver

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        setupToolBar()
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Observe the current weather
        lifecycleScope.launch {
            viewModel.currentWeather.collect {
                when (it) {
                    is CurrentWeatherState.Loading -> binding.currentWeatherProgressBar.visibility =
                        View.VISIBLE

                    is CurrentWeatherState.Success -> {
                        binding.currentWeatherProgressBar.visibility = View.GONE
                        val currentWeather = it.currentWeather
                        updateUI(currentWeather)
                        Log.d(TAG, "Current Weather: $currentWeather")
                    }

                    is CurrentWeatherState.Error -> {
                        binding.currentWeatherProgressBar.visibility = View.GONE
                        Log.e(TAG, "Error: ${it.message}")
                        Toast.makeText(requireActivity(), "Error: ${it.message}", Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.S)
    override fun onResume() {
        super.onResume()

        Log.d(TAG, "onResume")
        initUI()
        // Register the broadcast receiver
        locationBroadcastReceiver = LocationBroadcastReceiver(this)
        val intentFilter = IntentFilter("android.location.PROVIDERS_CHANGED")
        requireActivity().registerReceiver(
            locationBroadcastReceiver, intentFilter
        )
        getFreshLocation(locationCallback)
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop")
        requireActivity().unregisterReceiver(locationBroadcastReceiver)
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    private fun initUI() {
        binding.openLocationServicesButton.visibility =
            if (GPSUtils.isGPSEnabled(requireActivity())) View.GONE else View.VISIBLE

        binding.openLocationServicesButton.setOnClickListener { showEnableGPSDialog() }

    }

    private fun setupToolBar() {
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)


        // Drawer layout and navigation
        val toggle = ActionBarDrawerToggle(
            requireActivity(), binding.drawerLayout, binding.toolbar, 0, 0
        )
        binding.toolbar.setNavigationOnClickListener {
            if (binding.drawerLayout.isDrawerOpen(binding.navView)) binding.drawerLayout.closeDrawer(
                binding.navView
            )
            else binding.drawerLayout.openDrawer(binding.navView)

        }


        binding.drawerLayout.addDrawerListener(toggle)
        toggle.isDrawerIndicatorEnabled = false
        toggle.setHomeAsUpIndicator(R.drawable.icon_menu)
        toggle.syncState()

        NavigationUI.setupWithNavController(
            binding.navView, Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
        )

    }

    private fun updateUI(currentWeather: CurrentWeather) {
        binding.cityNameTextView.text = currentWeather.name

        binding.weatherTempTextView.text = "${currentWeather.main.temp.toInt()}"
        val tempMeasurements = when (viewModel.getUnit()) {
            UNITS.METRIC -> getString(R.string.celsius_measure)
            UNITS.IMPERIAL -> getString(R.string.fahrenheit_measure)
            UNITS.STANDARD -> getString(R.string.kelvin_measure)
        }

        // 17°C
        val animator = ValueAnimator.ofInt(0, currentWeather.main.temp.toInt())
        animator.duration = 1000
        animator.addUpdateListener { valueAnimator ->
            val value = valueAnimator.animatedValue as Int
            binding.weatherTempTextView.text = value.toString()
        }
        animator.start()
        binding.weatherTempMeasurementsTextView.text = tempMeasurements

        // Clear
        binding.weatherDescriptionTextView.text =
            currentWeather.weather[0].description.toCapitalizedWords()

        // H:18°C L: 12°C
        val maxTemp = currentWeather.main.tempMax.toInt()
        val minTemp = currentWeather.main.tempMin.toInt()
        val maxString = resources.getString(R.string.max)
        val minString = resources.getString(R.string.min)
        binding.highLowTempTextView.text =
            "$maxString: $maxTemp° ○ $minString: $minTemp°"


        Glide.with(requireActivity()).load(
            "https://openweathermap.org/img/wn/${currentWeather.weather[0].icon}.png"
        ).into(binding.weatherImageView)
    }


    @RequiresApi(Build.VERSION_CODES.S)
    private val requestLocationPermission =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { isGranted ->
            if (isGranted.containsValue(true)) {
                if (!GPSUtils.isLocationEnabled(requireActivity())) enableLocationService()
                getFreshLocation(locationCallback)
            } else {
                Toast.makeText(requireActivity(), "Permission denied", Toast.LENGTH_LONG).show()
            }
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
        Log.d(TAG, "Location is enabled")
        fusedLocationProviderClient.requestLocationUpdates(
            LocationRequest.Builder(DELAY_IN_LOCATION_REQUEST).apply {
                setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            }.build(), callback, Looper.myLooper()
        ).exception?.let {
            Log.e(TAG, "Error getting location: ${it.message}")
        }
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
    private fun getLocation() {
        if (PermissionUtils.checkPermission(requireActivity())) {
            if (!GPSUtils.isLocationEnabled(requireActivity()))
//                enableLocationService()
//                requestGPSOn(getGPSOnRequest)
            else getFreshLocation(locationCallback)
        } else requestLocationPermission.launch(permissions)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private val getGPSOnRequest =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
            if (GPSUtils.isLocationEnabled(requireActivity())) getFreshLocation(locationCallback)
            else Toast.makeText(
                requireActivity(), "Please enable location service", Toast.LENGTH_LONG
            ).show()
        }

    private fun requestGPSOn(request: ActivityResultLauncher<IntentSenderRequest>) {
        val locationRequest = LocationRequest.Builder(DELAY_IN_LOCATION_REQUEST).apply {
            setPriority(Priority.PRIORITY_HIGH_ACCURACY)
        }.build()

        val settingRequest = LocationSettingsRequest.Builder().run {
            addLocationRequest(locationRequest)
            build()
        }

        val settingsClient = LocationServices.getSettingsClient(requireContext())
        val task =
            settingsClient.checkLocationSettings(settingRequest)         //【fire and receive result】

        task.addOnFailureListener {                             //if GPS is not on currently
            val intentSender = (it as ResolvableApiException).resolution.intentSender
            val intentSenderRequest = IntentSenderRequest.Builder(intentSender).build()

            request.launch(intentSenderRequest)
        }
    }

    private fun showEnableGPSDialog() {
        val builder = MaterialAlertDialogBuilder(requireActivity())

        builder.setTitle(getString(R.string.enable_gps))
            .setMessage(getString(R.string.gps_is_disabled_do_you_want_to_enable_it))
            .setBackground(
                ResourcesCompat.getDrawable(
                    resources, R.drawable.dialog_background,
                    requireActivity().theme
                )
            )
            .setIcon(
                ResourcesCompat.getDrawable(
                    resources, R.drawable.icon_location,
                    requireActivity().theme
                )
            ).setCancelable(false)
            .setPositiveButton(getString(R.string.yes)) { _, _ ->

                val gpsOptionsIntent = Intent(
                    Settings.ACTION_LOCATION_SOURCE_SETTINGS
                )
                startActivity(gpsOptionsIntent)
            }.setNegativeButton(getString(R.string.no)) { dialog, _ -> dialog.cancel() }

        val alert: AlertDialog = builder.create()
        alert.show()
    }

    // Broadcast receiver callbacks
    @RequiresApi(Build.VERSION_CODES.S)
    override fun locationEnabled() {
        Log.d(TAG, "Broadcast receiver: Location is enabled")
        binding.openLocationServicesButton.visibility = View.GONE
        getFreshLocation(locationCallback)
    }

    override fun locationDisabled() {
        binding.openLocationServicesButton.visibility = View.VISIBLE
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

}


