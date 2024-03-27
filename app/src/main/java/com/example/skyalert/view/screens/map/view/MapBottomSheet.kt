package com.example.skyalert.view.screens.map.view

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
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
import com.example.skyalert.services.alarm.AndroidAlarmScheduler
import com.example.skyalert.services.alarm.model.AlarmItem
import com.example.skyalert.util.WeatherViewModelFactory
import com.example.skyalert.util.millisecondsToLocalDateTime
import com.example.skyalert.view.screens.map.viewModel.MapViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.Calendar

private const val TAG = "MapBottomSheet"

class MapBottomSheet(private val coord: Coord) : BottomSheetDialogFragment(),
    DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private val binding by lazy {
        FragmentMapBottomSheetBinding.inflate(layoutInflater, null, false)
    }

    private val alarm: AndroidAlarmScheduler by lazy {
        AndroidAlarmScheduler(requireContext().applicationContext)
    }
    private val alarmItem by lazy {
        AlarmItem(
            LocalDateTime.now().plusSeconds(10L), "This is a test alarm"
        )
    }

    private var day = 0
    private var month = 0
    private var year = 0
    private var hour = 0
    private var minute = 0


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


        binding.imageViewAlert.setOnClickListener {
            pickDate()
        }

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

    private fun pickDate() {
        val c = Calendar.getInstance()
        val dpd = DatePickerDialog(
            requireContext(),
            this,
            c.get(Calendar.YEAR),
            c.get(Calendar.MONTH),
            c.get(Calendar.DAY_OF_MONTH)
        )
        dpd.show()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val c = Calendar.getInstance()
        this.day = dayOfMonth
        this.month = month
        this.year = year
        val tpd = TimePickerDialog(
            requireContext(), this, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true
        )
        tpd.show()
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        this.hour = hourOfDay
        this.minute = minute
        val calendar = Calendar.getInstance().apply {
            set(
                year, month, day, hour, minute, 0
            )
            set(Calendar.MILLISECOND, 0)
        }

        Log.d(TAG, "onTimeSet: ${millisecondsToLocalDateTime(calendar.timeInMillis)}")

        alarmItem.time = millisecondsToLocalDateTime(calendar.timeInMillis)
        alarm.scheduleAlarm(alarmItem)
    }
}