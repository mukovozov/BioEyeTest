package com.example.bioeyetest.utils

import javax.inject.Inject

interface TimeProvider {
    val currentTimeMillis: Long
}

class TimeProviderImpl @Inject constructor() : TimeProvider {
    override val currentTimeMillis: Long
        get() = System.currentTimeMillis()
}