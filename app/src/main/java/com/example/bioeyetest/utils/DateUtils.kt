package com.example.bioeyetest.utils

import java.text.SimpleDateFormat
import java.util.*

private const val ISO_8601_WITHOUT_TIMEZONE_FORMAT = "yyyy-MM-dd HH:mm:ss"

@Synchronized
internal fun Long.toIso8601(): String {
    val cal = Calendar.getInstance()
    cal.timeInMillis = this
    val sdf = SimpleDateFormat(ISO_8601_WITHOUT_TIMEZONE_FORMAT, Locale.US)
    return sdf.format(cal.time)
}

@Synchronized
internal fun String.fromIso8601(timeZone: TimeZone): Long {
    val sdf = SimpleDateFormat(ISO_8601_WITHOUT_TIMEZONE_FORMAT, Locale.US)
    sdf.timeZone = timeZone
    return sdf.parse(this)?.time ?: 0
}