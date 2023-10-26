package com.example.bioeyetest.core.date_time

import javax.inject.Inject

interface TimeProvider {
    val currentTimeMillis: Long
}

class TimeProviderImpl @Inject constructor() : TimeProvider {
    override val currentTimeMillis: Long
        get() = System.currentTimeMillis()
}