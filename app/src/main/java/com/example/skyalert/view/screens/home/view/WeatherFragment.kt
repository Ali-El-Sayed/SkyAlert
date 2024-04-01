package com.example.skyalert.view.screens.home.view

import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.skyalert.R
import com.example.skyalert.dataSource.local.WeatherLocalDatasourceImpl
import com.example.skyalert.dataSource.local.db.WeatherDatabase
import com.example.skyalert.dataSource.local.localStorage.LocalStorage
import com.example.skyalert.dataSource.local.sharedPref.SharedPreferenceImpl
import com.example.skyalert.dataSource.remote.WeatherRemoteDatasource
import com.example.skyalert.databinding.FragmentWeatherBinding
import com.example.skyalert.model.remote.Coord
import com.example.skyalert.model.remote.CurrentWeather
import com.example.skyalert.model.remote.Day
import com.example.skyalert.network.NetworkHelper
import com.example.skyalert.network.RetrofitClient
import com.example.skyalert.network.UNITS
import com.example.skyalert.network.model.CurrentWeatherState
import com.example.skyalert.network.model.FiveDaysForecastState
import com.example.skyalert.repository.WeatherRepo
import com.example.skyalert.services.broadcastReceiver.locationReceiver.LocationBroadcastReceiver
import com.example.skyalert.services.broadcastReceiver.locationReceiver.OnLocationChange
import com.example.skyalert.util.GPS_NETWORK_Utils
import com.example.skyalert.util.WeatherViewModelFactory
import com.example.skyalert.util.toCapitalizedWords
import com.example.skyalert.view.animation.NumberAnimation
import com.example.skyalert.view.screens.home.adapters.RvFiveDaysForecastAdapter
import com.example.skyalert.view.screens.home.adapters.RvHourlyForecastAdapter
import com.example.skyalert.view.screens.home.viewModel.WeatherScreenViewModel
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.roundToInt


class WeatherFragment : Fragment(), OnLocationChange {
    private val binding: FragmentWeatherBinding by lazy {
        FragmentWeatherBinding.inflate(layoutInflater)
    }
    private var longitude: Double = 0.0
    private var latitude: Double = 0.0
    private val TAG = "WeatherFragment"
    private val DELAY_IN_LOCATION_REQUEST = 2000000L
    private val viewModel: WeatherScreenViewModel by lazy {
        val remoteDataSource = WeatherRemoteDatasource.getInstance(RetrofitClient.apiService)
        val dao = WeatherDatabase.getInstance(requireContext().applicationContext).weatherDao()
        val sharedPref = SharedPreferenceImpl.getInstance(requireActivity().applicationContext)
        val localStorage = LocalStorage.getInstance(requireActivity().applicationContext)
        val localDatasource = WeatherLocalDatasourceImpl.getInstance(
            dao, sharedPref, localStorage
        )
        val repo = WeatherRepo.getInstance(
            remoteDataSource, localDatasource
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
                viewModel.setDefaultLocation(Coord(latitude, longitude))
                viewModel.getCurrentWeather(GPS_NETWORK_Utils.isNetworkConnected(requireActivity()))
                viewModel.getHourlyWeather(40, GPS_NETWORK_Utils.isNetworkConnected(requireActivity()))
            }
        }
    }
    private val fusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(requireActivity())
    }

    private lateinit var locationBroadcastReceiver: LocationBroadcastReceiver

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        setupToolBar()
        return binding.root
    }


    @RequiresApi(Build.VERSION_CODES.S)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Observe the current weather & hourly weather
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.currentWeather.collect {
                    when (it) {
                        is CurrentWeatherState.Loading -> binding.currentWeatherProgressBar.visibility = View.VISIBLE

                        is CurrentWeatherState.Success -> {
                            binding.currentWeatherProgressBar.visibility = View.GONE
                            val currentWeather = it.currentWeather
                            if (!currentWeather.isCurrent) {
                                currentWeather.isCurrent = true
                                viewModel.saveCurrentWeather(currentWeather)
                            }
                            updateToolbar(currentWeather)
                            updateCurrentDetails(currentWeather)
                            Log.d(TAG, "Current Weather: $currentWeather")
                        }

                        is CurrentWeatherState.Error -> {
                            binding.currentWeatherProgressBar.visibility = View.GONE
                            Log.e(TAG, "Error Here: ${it.message}")
                            Toast.makeText(
                                requireActivity(), "Error: ${it.message}", Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }

            }
        }
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.hourlyWeather.collect {
                    when (it) {
                        is FiveDaysForecastState.Loading -> binding.currentWeatherProgressBar.visibility = View.VISIBLE

                        is FiveDaysForecastState.Success -> {
                            binding.currentWeatherProgressBar.visibility = View.GONE
                            val today = it.data.list[0]
                            viewModel.saveFiveDaysForecast(it.data)
                            // Set up the hourly forecast recycler view
                            updateHourlyList(it, today)
                            // Set up the five days forecast recycler view
                            updateFiveDaysList(it, today)
                        }

                        is FiveDaysForecastState.Error -> {
                            binding.currentWeatherProgressBar.visibility = View.GONE
                            Log.e(TAG, "Error Here: ${it.message}")
                        }

                    }
                }
            }
        }
    }

    private fun updateFiveDaysList(
        it: FiveDaysForecastState.Success, today: Day
    ) {
        val todayName = SimpleDateFormat("EEEE", Locale.getDefault()).format(today.dt.toLong() * 1000)
        val fiveDaysForecast = it.data.list.filter { day ->
            val dayName = SimpleDateFormat("EEEE", Locale.getDefault()).format(day.dt.toLong() * 1000)
            dayName != todayName && day.dtTxt.contains("12:00:00")
        }.toMutableList()
        fiveDaysForecast.add(0, today)
        val fiveDaysAdapter = RvFiveDaysForecastAdapter()
        fiveDaysAdapter.submitList(fiveDaysForecast)
        binding.recyclerViewFiveDaysForecast.layoutManager = LinearLayoutManager(
            requireActivity(), LinearLayoutManager.VERTICAL, false
        )
        binding.recyclerViewFiveDaysForecast.adapter = fiveDaysAdapter
    }

    private fun updateHourlyList(
        it: FiveDaysForecastState.Success, today: Day
    ) {
        val todayName = SimpleDateFormat(
            "EEEE", Locale.getDefault()
        ).format(today.dt.toLong() * 1000)
        val adapter = RvHourlyForecastAdapter()
        val daysList = mutableListOf<Day>()
        for (i in it.data.list.indices) {
            Log.d(TAG, "Day: ${it.data.list[i]}")
            val day = it.data.list[i]
            val dayName = SimpleDateFormat("EEEE", Locale.getDefault()).format(day.dt.toLong() * 1000)
            if (dayName == todayName) daysList.add(day)
            else {
                val newDay = it.data.list[i]
                newDay.sys.sunrise = it.data.city.sunrise
                daysList.add(newDay)
                break
            }
        }
        adapter.submitList(daysList)
        binding.recyclerViewHourlyForecast.layoutManager = LinearLayoutManager(
            requireActivity(), LinearLayoutManager.HORIZONTAL, false
        )
        binding.recyclerViewHourlyForecast.adapter = adapter
    }

    private fun updateCurrentDetails(
        currentWeather: CurrentWeather
    ) {
        val windMeasurement = getString(R.string.m_s)
        val pressureMeasurement = getString(R.string.hpa)

        val dayNumber = SimpleDateFormat(
            "dd", Locale.getDefault()
        ).format(currentWeather.dt.toLong() * 1000)
        val monthName = SimpleDateFormat(
            "MMMM", Locale.getDefault()
        ).format(currentWeather.dt.toLong() * 1000).substring(0, 3)
        val dayName = SimpleDateFormat(
            "EEEE", Locale.getDefault()
        ).format(currentWeather.dt.toLong() * 1000)

        // Set the current weather icon
        Glide.with(requireActivity()).load(NetworkHelper.getIconUrl(currentWeather.weather[0].icon))
            .into(binding.currentWeatherDetails.imageViewWeatherIcon)
        // Date
        binding.currentWeatherDetails.date.text = "$dayNumber, $monthName $dayName"
        // humidity
        binding.currentWeatherDetails.humidity.text = "${currentWeather.main.humidity}%"
        // wind speed
        binding.currentWeatherDetails.wind.text = "${currentWeather.wind.speed} $windMeasurement"
        // pressure
        binding.currentWeatherDetails.pressure.text = "${currentWeather.main.pressure} $pressureMeasurement"
        // real feel
        binding.currentWeatherDetails.realFeel.text = "${currentWeather.main.feelsLike.roundToInt()}°"
    }

    private fun updateToolbar(currentWeather: CurrentWeather) {
        binding.cityNameTextView.text = currentWeather.name

        binding.weatherTempTextView.text = "${currentWeather.main.temp.toInt()}"
        val tempMeasurements = when (currentWeather.unit) {
            UNITS.METRIC -> getString(R.string.celsius_measure)
            UNITS.IMPERIAL -> getString(R.string.fahrenheit_measure)
            UNITS.STANDARD -> getString(R.string.kelvin_measure)
        }

        // animate the temperature from 0 to current temperature
        // 17°C
        NumberAnimation.fromZeroToValueText(
            currentWeather.main.feelsLike.roundToInt(), binding.weatherTempTextView
        )

        binding.weatherTempMeasurementsTextView.text = tempMeasurements

        // Clear
        binding.weatherDescriptionTextView.text = currentWeather.weather[0].description.toCapitalizedWords()

        // H:18° L: 12°
        val maxTemp = currentWeather.main.tempMax.roundToInt()
        val minTemp = currentWeather.main.tempMin.roundToInt()
        val maxString = resources.getString(R.string.max)
        val minString = resources.getString(R.string.min)
        binding.highLowTempTextView.text = "$maxString: $maxTemp° ○ $minString: $minTemp°"

        Glide.with(requireActivity()).load(NetworkHelper.getIconUrl(currentWeather.weather[0].icon))
            .into(binding.weatherImageView)
    }


    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")
        initUI()
        // Register the broadcast receiver
        locationBroadcastReceiver = LocationBroadcastReceiver(this)
        getFreshLocation(locationCallback)
        val intentFilter = IntentFilter("android.location.PROVIDERS_CHANGED")
        requireActivity().registerReceiver(
            locationBroadcastReceiver, intentFilter
        )
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop")
        requireActivity().unregisterReceiver(locationBroadcastReceiver)
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    private fun initUI() {

        // check if location is enabled or not and show the button to enable location
        if (!GPS_NETWORK_Utils.isLocationEnabled(requireActivity())) binding.openLocationServicesButton.visibility = View.VISIBLE

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
        toggle.setHomeAsUpIndicator(R.drawable.ic_menu)
        toggle.syncState()

        NavigationUI.setupWithNavController(
            binding.navView, Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
        )

    }

    @SuppressLint("MissingPermission")
    private fun getFreshLocation(callback: LocationCallback) {
        Toast.makeText(requireActivity(), "Getting location", Toast.LENGTH_SHORT).show()
        Log.d(TAG, "Getting location")
        fusedLocationProviderClient.requestLocationUpdates(
            LocationRequest.Builder(DELAY_IN_LOCATION_REQUEST).apply {
                setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            }.build(), callback, Looper.myLooper()
        ).exception?.let {
            Log.e(TAG, "Error getting location: ${it.message}")
        }
    }

    private fun showEnableGPSDialog() {
        val builder = MaterialAlertDialogBuilder(requireActivity())

        builder.setTitle(getString(R.string.enable_gps)).setMessage(getString(R.string.gps_is_disabled_do_you_want_to_enable_it))
            .setBackground(
                ResourcesCompat.getDrawable(
                    resources, R.drawable.dialog_background, requireActivity().theme
                )
            ).setIcon(
                ResourcesCompat.getDrawable(
                    resources, R.drawable.ic_location, requireActivity().theme
                )
            ).setCancelable(false).setPositiveButton(getString(R.string.yes)) { _, _ ->

                val gpsOptionsIntent = Intent(
                    Settings.ACTION_LOCATION_SOURCE_SETTINGS
                )
                startActivity(gpsOptionsIntent)
            }.setNegativeButton(getString(R.string.no)) { dialog, _ -> dialog.cancel() }.create().show()
    }

    /**
     *  Broadcast receiver for location change events
     * */
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


