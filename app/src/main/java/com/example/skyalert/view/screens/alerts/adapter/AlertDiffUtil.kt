package com.example.skyalert.view.screens.alerts.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.skyalert.services.alarm.model.Alert

class AlertDiffUtil : DiffUtil.ItemCallback<Alert>() {
    override fun areItemsTheSame(oldItem: Alert, newItem: Alert): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Alert, newItem: Alert): Boolean {
        return oldItem.uuid == newItem.uuid
    }

}