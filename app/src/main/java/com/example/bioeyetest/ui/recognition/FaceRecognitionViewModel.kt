package com.example.bioeyetest.ui.recognition

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bioeyetest.ui.Navigator
import com.example.bioeyetest.R
import com.example.bioeyetest.core.DispatchersProvider
import com.example.bioeyetest.data.face_recognition.FaceRecognitionResult
import com.example.bioeyetest.domain.face_recognition.FaceRecognitionProcessor
import com.example.bioeyetest.data.sensor.LightSensorManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import javax.inject.Inject

@HiltViewModel
class FaceRecognitionViewModel @Inject constructor(
    private val lightSensorManager: LightSensorManager,
    private val faceRecognitionProcessor: FaceRecognitionProcessor,
    private val navigator: Navigator,
    private val dispatchersProvider: DispatchersProvider,
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
                .map {
                    when (it) {
                        FaceRecognitionResult.NO_FACE_DETECTED -> UiFaceRecognitionResult.NO_FACE_DETECTED
                        FaceRecognitionResult.FACE_DETECTED -> UiFaceRecognitionResult.FACE_DETECTED
                    }
                }
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
            val lux = lightSensorManager.requestSingleUpdate()
            val newViewState = when {
                lux < MIN_LIGHT_LUX -> {
                    FaceRecognitionViewState.PreparationFailed(PreparationFailedReason.ROOM_IS_TOO_DARK)
                }

                lux > MAX_LIGHT_LUX -> {
                    FaceRecognitionViewState.PreparationFailed(PreparationFailedReason.ROOM_IS_TOO_BRIGHT)
                }

                else -> {
                    FaceRecognitionViewState.Recognition(UiFaceRecognitionResult.NO_RESULT)
                }
            }

            _viewState.value = newViewState
        }
    }

    private fun startPeriodicRecognition() {
        timerJob = viewModelScope.launch(dispatchersProvider.default) {
            try {
                withTimeout(FACE_RECOGNITION_SESSION_MAX_DURATION_MILLIS) {
                    while (true) {
                        _events.tryEmit(FaceRecognitionEvents.ProvideNextFrame)

                        delay(FACE_RECOGNITION_PERIOD_MILLIS)
                    }
                }
            } catch (timeout: TimeoutCancellationException) {
                timerJob = null
                goToSessionSummaryScreen()
            }
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
        private const val FACE_RECOGNITION_SESSION_MAX_DURATION_MILLIS = 30_000L
    }
}