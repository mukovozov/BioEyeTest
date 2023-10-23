package com.example.bioeyetest.ui.recognition

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bioeyetest.sensor.LightSensorProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FaceRecognitionViewModel @Inject constructor(
    private val lightSensorProvider: LightSensorProvider
) : ViewModel() {

    val viewState: StateFlow<FaceRecognitionViewState>
        get() = _viewState

    private val _viewState = MutableStateFlow<FaceRecognitionViewState>(FaceRecognitionViewState.Preparing)

    init {
        checkLightConditions()
    }

    fun onRetryButtonClicked() {
        checkLightConditions()
    }

    private fun checkLightConditions() {
        viewModelScope.launch {
            lightSensorProvider.lightSensorLux.collect { lux ->
                val newViewState = when {
                    lux < MIN_LIGHT_LUX -> {
                        FaceRecognitionViewState.PreparationFailed(PreparationFailedReason.ROOM_IS_TOO_DARK)
                    }

                    lux > MAX_LIGHT_LUX -> {
                        FaceRecognitionViewState.PreparationFailed(PreparationFailedReason.ROOM_IS_TOO_BRIGHT)
                    }

                    else -> {
                        FaceRecognitionViewState.Recognition
                    }
                }

                lightSensorProvider.stop()

                _viewState.value = newViewState
            }
        }

        lightSensorProvider.start()
    }

    companion object {
        private const val TAG = "FaceRecognitionViewModel"
        private const val MIN_LIGHT_LUX = 20
        private const val MAX_LIGHT_LUX = 1000
    }
}