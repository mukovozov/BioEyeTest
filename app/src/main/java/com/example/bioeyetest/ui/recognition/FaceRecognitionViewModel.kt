package com.example.bioeyetest.ui.recognition

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bioeyetest.Navigator
import com.example.bioeyetest.R
import com.example.bioeyetest.face_recognition.FaceRecognitionProcessor
import com.example.bioeyetest.face_recognition.FaceRecognitionResult
import com.example.bioeyetest.sensor.LightSensorProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FaceRecognitionViewModel @Inject constructor(
    private val lightSensorProvider: LightSensorProvider,
    private val faceRecognitionProcessor: FaceRecognitionProcessor,
    private val navigator: Navigator,
) : ViewModel() {

    val viewState: StateFlow<FaceRecognitionViewState>
        get() = _viewState

    private val _viewState = MutableStateFlow<FaceRecognitionViewState>(FaceRecognitionViewState.Preparing)

    init {
        checkLightConditions()
    }

    fun onNewFrameReady(frame: Bitmap) {
        viewModelScope.launch {
            faceRecognitionProcessor.process(frame)
                .onSuccess { result ->
                    _viewState.value = FaceRecognitionViewState.Recognition(result)
                }
                .onFailure { error ->
                    Log.e(TAG, "onNewFrameReady: ${error.message}", error)
                }
        }
    }

    fun onCompleteSessionButtonClicked() {
        navigator.navigateTo(R.id.action_face_recognition_to_session_summary)
    }

    fun onRetryButtonClicked() {
        MIN_LIGHT_LUX = 20
        checkLightConditions()
    }

    private fun checkLightConditions() {
        viewModelScope.launch {
            val lux = lightSensorProvider.requestSingleUpdate()
            val newViewState = when {
                lux < MIN_LIGHT_LUX -> {
                    FaceRecognitionViewState.PreparationFailed(PreparationFailedReason.ROOM_IS_TOO_DARK)
                }

                lux > MAX_LIGHT_LUX -> {
                    FaceRecognitionViewState.PreparationFailed(PreparationFailedReason.ROOM_IS_TOO_BRIGHT)
                }

                else -> {
                    FaceRecognitionViewState.Recognition(FaceRecognitionResult.NO_RESULT)
                }
            }

            _viewState.value = newViewState
        }
    }

    companion object {
        private const val TAG = "FaceRecognitionViewModel"

        private var MIN_LIGHT_LUX = 500
        private const val MAX_LIGHT_LUX = 1000
        private const val FACE_RECOGNITION_PERIOD_MILLIS = 1000L
        private const val FACE_RECOGNITION_SESSION_MAX_DURATION_SECONDS = 30L
    }
}