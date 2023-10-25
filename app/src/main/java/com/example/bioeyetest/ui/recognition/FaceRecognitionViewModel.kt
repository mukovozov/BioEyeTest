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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.concurrent.fixedRateTimer

@HiltViewModel
class FaceRecognitionViewModel @Inject constructor(
    private val lightSensorProvider: LightSensorProvider,
    private val faceRecognitionProcessor: FaceRecognitionProcessor,
    private val navigator: Navigator,
) : ViewModel() {

    private val _viewState = MutableStateFlow<FaceRecognitionViewState>(FaceRecognitionViewState.Preparing)
    val viewState: StateFlow<FaceRecognitionViewState>
        get() = _viewState

    private val _events = MutableSharedFlow<FaceRecognitionEvents>(extraBufferCapacity = 1)
    val events: SharedFlow<FaceRecognitionEvents>
        get() = _events

    private var timerJob: Job? = null
        set(value) {
            field?.cancel()
            field = value
        }

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
        goToSessionSummaryScreen()
    }

    fun onRetryButtonClicked() {
        checkLightConditions()
    }

    fun onCameraReady() {
        startPeriodicRecognition()
    }

    fun onStop() {
        timerJob = null
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

    private fun startPeriodicRecognition() {
        timerJob = viewModelScope.launch(Dispatchers.Default) {
            var count = 0
            while (count < FACE_RECOGNITION_SESSION_MAX_DURATION_SECONDS) {
                count++
                _events.tryEmit(FaceRecognitionEvents.ProvideNextFrame)

                delay(FACE_RECOGNITION_PERIOD_MILLIS)
            }

            timerJob = null
            goToSessionSummaryScreen()
        }
    }

    private fun goToSessionSummaryScreen() {
        navigator.navigateTo(R.id.action_face_recognition_to_session_summary)
    }

    companion object {
        private const val TAG = "FaceRecognitionViewModel"

        private const val MIN_LIGHT_LUX = 20
        private const val MAX_LIGHT_LUX = 1000
        private const val FACE_RECOGNITION_PERIOD_MILLIS = 1000L
        private const val FACE_RECOGNITION_SESSION_MAX_DURATION_SECONDS = 30L
    }
}