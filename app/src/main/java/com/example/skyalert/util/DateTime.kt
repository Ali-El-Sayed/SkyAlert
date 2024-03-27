package com.example.skyalert.util

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId


fun millisecondsToLocalDateTime(milliseconds: Long): LocalDateTime {
    return LocalDateTime.ofInstant(
        Instant.ofEpochMilli(milliseconds),
        ZoneId.systemDefault()
    )
}