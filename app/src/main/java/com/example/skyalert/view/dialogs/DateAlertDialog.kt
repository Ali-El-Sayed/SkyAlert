package com.example.skyalert.view.dialogs

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.core.view.children
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import com.example.skyalert.R
import com.example.skyalert.dataSource.local.WeatherLocalDatasourceImpl
import com.example.skyalert.dataSource.local.db.WeatherDatabase
import com.example.skyalert.dataSource.local.sharedPref.SharedPreferenceImpl
import com.example.skyalert.dataSource.remote.WeatherRemoteDatasource
import com.example.skyalert.databinding.AlertDialogBinding
import com.example.skyalert.interfaces.Callback
import com.example.skyalert.interfaces.OnAlertDialogCallback
import com.example.skyalert.network.RetrofitClient
import com.example.skyalert.repository.WeatherRepo
import com.example.skyalert.services.workManager.DialogAlertManager
import com.example.skyalert.util.WeatherViewModelFactory
import com.example.skyalert.util.getDayName
import com.example.skyalert.util.getHour24Format
import com.example.skyalert.util.getMillisecondsFromDate
import com.example.skyalert.util.getMinute
import com.example.skyalert.util.getMonthName
import com.example.skyalert.view.screens.map.MAP_CONSTANTS
import com.example.skyalert.view.screens.map.viewModel.MapViewModel
import java.time.LocalDate
import java.util.Calendar
import java.util.UUID


private const val TAG = "AlertDialog"

class DateAlertDialog(private val onAlertDialogCallback: OnAlertDialogCallback) : DialogFragment(),
    DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, Callback {
    private val binding by lazy {
        AlertDialogBinding.inflate(layoutInflater)
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
        ViewModelProvider(requireActivity(), factory)[MapViewModel::class.java]
    }

    private var day = 0
    private var month = 0
    private var year = 0
    private var hour = getHour24Format()
    private var minute = getMinute()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentDate = LocalDate.now()
        day = currentDate.dayOfMonth
        month = currentDate.monthValue
        year = currentDate.year

        binding.tvAlertTime.text = getFormattedDate(requireContext(), year, month, day, hour, minute)
        binding.tvAlertTime.setOnClickListener { pickDate() }
        binding.btnNotifyMe.setOnClickListener {
            onFinished()
            dismiss()
        }

    }

    private fun pickDate() {
        val c = Calendar.getInstance()
        val dpd = DatePickerDialog(
            requireContext(), this, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)
        )
        dpd.show()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val c = Calendar.getInstance()
        this.day = dayOfMonth
        this.month = month + 1
        this.year = year
        Log.d(TAG, "onDateSet: $year $month $dayOfMonth")
        val tpd = TimePickerDialog(
            requireContext(), this, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true
        )
        tpd.show()
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        this.hour = hourOfDay
        this.minute = minute.toString()
        binding.tvAlertTime.text = getFormattedDate(
            requireContext(), year, month, day, hourOfDay, minute.toString()
        )
    }

    private fun getFormattedDate(
        context: Context, year: Int, month: Int, day: Int, hours: Int = 0, minutes: String = "00"
    ): String {
        val dayName = getDayName(day, month, year).substring(0, 3)
        val monthName = getMonthName(month).substring(0, 3)
        val time = if (hours < 12) {
            "$hours:$minutes ${context.resources.getString(R.string.am)}"
        } else {
            "${hours - 12}:$minutes ${context.resources.getString(R.string.pm)}"
        }
        return "$day $dayName, $monthName\n$time"
    }


    override fun onFinished() {
        val checked = binding.chipGroup.checkedChipId
        val v = binding.chipGroup.children.find { it.id == checked }
        viewModel.alert.apply {
            uuid = UUID.randomUUID()
            time = getMillisecondsFromDate(year, month, day, hour, minute.toInt())
        }
        if (v?.tag?.equals(resources.getString(R.string.alert)) == true) {
            // get current time
            val currentTimeMillis = System.currentTimeMillis()
            // create the data for the work manager to pass the location of the alert
            val data = Data.Builder().putDouble(
                MAP_CONSTANTS.MAP_LON, viewModel.alert.lon
            ).putDouble(MAP_CONSTANTS.MAP_LAT, viewModel.alert.lat).build()
            // create the constraints for the work manager to run only when the network is connected
            val constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
            // get the due date in milliseconds and set it to the alert
            viewModel.alert.time = getMillisecondsFromDate(year, month, day, hour, minute.toInt())
            // create the work request for the work manager
            val request = OneTimeWorkRequestBuilder<DialogAlertManager>().setInitialDelay(
                viewModel.alert.time - currentTimeMillis, java.util.concurrent.TimeUnit.MILLISECONDS
            ).setInputData(data).setConstraints(constraints).build()
            // enqueue the work request
            onAlertDialogCallback.createDialog(request)
        } else if (v?.tag?.equals(resources.getString(R.string.notification)) == true)
            onAlertDialogCallback.createNotification()

    }

}