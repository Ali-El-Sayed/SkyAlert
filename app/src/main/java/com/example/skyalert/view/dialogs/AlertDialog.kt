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
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.children
import androidx.fragment.app.DialogFragment
import com.example.skyalert.R
import com.example.skyalert.databinding.AlertDialogBinding
import com.example.skyalert.interfaces.Callback
import com.example.skyalert.services.alarm.AndroidAlarmScheduler
import com.example.skyalert.services.alarm.model.AlarmItem
import com.example.skyalert.util.getDayName
import com.example.skyalert.util.getHour24Format
import com.example.skyalert.util.getMillisecondsFromDate
import com.example.skyalert.util.getMinute
import com.example.skyalert.util.millisecondsToLocalDateTime
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month
import java.time.format.TextStyle
import java.util.Calendar
import java.util.Locale


private const val TAG = "AlertDialog"

class AlertDialog : DialogFragment(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener, Callback {
    private val binding by lazy {
        AlertDialogBinding.inflate(layoutInflater)
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
    private var hour = getHour24Format()
    private var minute = getMinute()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding.root.background = ResourcesCompat.getDrawable(
            resources, R.drawable.dialog_background, requireActivity().theme
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentDate = LocalDate.now()
        day = currentDate.dayOfMonth
        month = currentDate.monthValue
        year = currentDate.year

        binding.tvAlertTime.text =
            getFormattedDate(requireContext(), year, month, day, hour, minute)
        binding.tvAlertTime.setOnClickListener { pickDate() }
        binding.btnNotifyMe.setOnClickListener {
            onFinished()
            dismiss()
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
        return "$day $dayName, $monthName $time"
    }


    private fun getMonthName(month: Int): String {
        return Month.of(month).getDisplayName(TextStyle.FULL, Locale.getDefault())
    }


    override fun onFinished() {
        val checked = binding.chipGroup.checkedChipId
        val v = binding.chipGroup.children.find { it.id == checked }
        if (v?.tag?.equals(resources.getString(R.string.alert)) == true) {

        } else if (v?.tag?.equals(resources.getString(R.string.notification)) == true) {
            alarmItem.time = millisecondsToLocalDateTime(
                getMillisecondsFromDate(
                    year, month, day, hour, minute.toInt()
                )
            )
            alarm.scheduleAlarm(alarmItem)
        }
    }

}