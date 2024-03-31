package com.example.skyalert.services.alarm.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull
import java.util.UUID

@Entity(tableName = "alert_table")
data class Alert(
    @PrimaryKey @NotNull var uuid: UUID = UUID.randomUUID(),
    var time: Long = 0,
    var message: String = "",
    var alertType: ALERT_TYPE = ALERT_TYPE.DIALOG,
    var city: String = "",
    var lat: Double = 0.0,
    var lon: Double = 0.0,
)
