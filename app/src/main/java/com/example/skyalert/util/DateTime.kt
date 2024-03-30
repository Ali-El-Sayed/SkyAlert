package com.example.skyalert.util

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Month
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale


/**
 *  Get the current date from the given milliseconds
 *  @param milliseconds the date in milliseconds ex. 1620000000000
 *  @return the date in the format ex. 2021-05-03T12:00:00
 * */
fun millisecondsToLocalDateTime(milliseconds: Long): LocalDateTime {
    return LocalDateTime.ofInstant(
        Instant.ofEpochMilli(milliseconds), ZoneId.systemDefault()
    )
}

/**
 * Get the current date in milliseconds
 * @param year the year
 * @param month the month
 * @param day the day
 * @param hour the hour
 * @param minute the minute
 * @return the date in milliseconds ex. 1620000000000
 * */

fun getMillisecondsFromDate(year: Int, month: Int, day: Int, hour: Int, minute: Int): Long {
    val localDateTime = LocalDateTime.of(year, month, day, hour, minute)
    return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
}


/**
 *  Get the current year ex. 2021
 * */
fun getHour24Format(): Int = LocalDateTime.now().hour

/**
 *  Get the current minute
 * */
fun getMinute(): String = LocalDateTime.now().minute.toString()

/**
 *  Get the formatted date string
 *  @param month the month
 *  @return Month name ex. January
 */

fun getMonthName(month: Int): String {
    return Month.of(month).getDisplayName(TextStyle.FULL, Locale.getDefault())
}


/**
 *  Get the day of the week name from the given date
 *  @param dayOfMonth the day of the month
 *  @param month the month of the year
 *  @param year the year
 *  @return the name of the day of the week ex. Monday
 */
fun getDayName(dayOfMonth: Int, month: Int, year: Int): String {
    val date = LocalDate.of(year, month, dayOfMonth)
    val dayOfWeek = date.dayOfWeek
    return dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
}

/**
 *  Get the current time in 12-hour format
 *  @return the current time in 12-hour format ex. 12:00 AM
 */
fun getCurrentTime(): String {
    val currentTime = LocalTime.now()
    val formatter = DateTimeFormatter.ofPattern("hh:mm a")
    return currentTime.format(formatter)
}