package com.example.skyalert.services.alarm.model

import java.time.LocalDateTime

data class AlarmItem(
    var time: LocalDateTime,
    val message: String
)
