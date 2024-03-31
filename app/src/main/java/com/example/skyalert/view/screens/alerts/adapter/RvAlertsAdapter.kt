package com.example.skyalert.view.screens.alerts.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.skyalert.R
import com.example.skyalert.databinding.AlertCardBinding
import com.example.skyalert.interfaces.OnAlertClickListener
import com.example.skyalert.services.alarm.model.ALERT_TYPE
import com.example.skyalert.services.alarm.model.Alert
import com.example.skyalert.util.millisecondsToLocalDateTime

class RvAlertsAdapter(private val onAlertClickListener: OnAlertClickListener) :
    ListAdapter<Alert, RvAlertsAdapter.AlertViewHolder>(AlertDiffUtil()) {
    private lateinit var binding: AlertCardBinding

    class AlertViewHolder(binding: AlertCardBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertViewHolder {
        val inflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = AlertCardBinding.inflate(inflater, parent, false)
        return AlertViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlertViewHolder, position: Int) {
        val alert = getItem(position)
        binding.tvCity.text = alert.city
        binding.tvDate.text = millisecondsToLocalDateTime(alert.time).toString()

        when (alert.alertType) {
            ALERT_TYPE.NOTIFICATION -> binding.ivAlertIcon.setImageResource(R.drawable.ic_notification)
            ALERT_TYPE.DIALOG -> binding.ivAlertIcon.setImageResource(R.drawable.ic_dialog)
        }
        binding.ivTrash.setOnClickListener { onAlertClickListener.onAlertClick(alert) }
    }
}
