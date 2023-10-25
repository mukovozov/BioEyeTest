package com.example.bioeyetest.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject

interface LightSensorProvider {
    val lightSensorLux: SharedFlow<Float>
    fun start()
    fun stop()

//    suspend fun requestSingleUpdate(): Float
}

class LightSensorProviderImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : LightSensorProvider {
    override val lightSensorLux = MutableSharedFlow<Float>(extraBufferCapacity = 1)

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private val lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

    private val listener = object : SensorEventListener {
        override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
            // Do nothing
        }

        override fun onSensorChanged(sensorEvent: SensorEvent?) {
            sensorEvent?.values?.get(0)?.let {
                lightSensorLux.tryEmit(it)
            }
        }
    }

    override fun start() {
        registerListener()
    }

    override fun stop() {
        unregisterListener()
    }

//    override suspend fun requestSingleUpdate(): Float {
//
//    }

    private fun registerListener() {
        sensorManager.registerListener(
            listener,
            lightSensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    private fun unregisterListener() {
        sensorManager.unregisterListener(listener, lightSensor)
    }
}