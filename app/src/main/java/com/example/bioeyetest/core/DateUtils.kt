package com.example.bioeyetest.core

import java.text.SimpleDateFormat
import java.util.*

private const val ISO_8601_WITHOUT_TIMEZONE_FORMAT = "yyyy-MM-dd HH:mm:ss"

internal fun Long.format(pattern: String = ISO_8601_WITHOUT_TIMEZONE_FORMAT): String {
    val cal = Calendar.getInstance()
    cal.timeInMillis = this
    val sdf = SimpleDateFormat(pattern, Locale.US)
    return sdf.format(cal.time)
}