package com.example.bioeyetest.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.example.bioeyetest.utils.DispatchersProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

interface LightSensorManager {
    val lightSensorLux: SharedFlow<Float>
    fun startUpdates()
    fun stopUpdates()

    suspend fun requestSingleUpdate(): Float
}

class LightSensorManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dispatchersProvider: DispatchersProvider,
) : LightSensorManager {
    override val lightSensorLux = MutableSharedFlow<Float>(extraBufferCapacity = 1)

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private val lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

    private val listener = object : SensorEventListener {
        override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
            // Do nothing
        }

        override fun onSensorChanged(sensorEvent: SensorEvent?) {
            sensorEvent?.values?.firstOrNull()?.let {
                lightSensorLux.tryEmit(it)
            }
        }
    }

    override fun startUpdates() {
        sensorManager.registerListener(listener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun stopUpdates() {
        sensorManager.unregisterListener(listener)
    }

    override suspend fun requestSingleUpdate(): Float {
        return withContext(dispatchersProvider.default) {
            makeSingleRequest()
        }
    }

    private suspend fun makeSingleRequest(): Float {
        return suspendCoroutine { continuation ->
            sensorManager.registerListener(object : SensorEventListener {
                override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
                    // Do nothing
                }

                override fun onSensorChanged(sensorEvent: SensorEvent?) {
                    sensorEvent?.values?.firstOrNull()?.let { value ->
                        lightSensorLux.tryEmit(value)
                        sensorManager.unregisterListener(this)
                        continuation.resume(value)
                    }
                }
            }, lightSensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }
}