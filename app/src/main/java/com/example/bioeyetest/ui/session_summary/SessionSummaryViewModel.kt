package com.example.bioeyetest.ui.session_summary

import androidx.lifecycle.ViewModel
import com.example.bioeyetest.face_recognition.FaceRecognitionManager
import com.example.bioeyetest.face_recognition.FaceRecognitionResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SessionSummaryViewModel @Inject constructor(
    private val faceRecognitionManager: FaceRecognitionManager
) : ViewModel() {

    val viewState: StateFlow<SessionSummaryViewState>
        get() = _viewState

    private val _viewState = MutableStateFlow(SessionSummaryViewState())

    init {
        val processedFrames = faceRecognitionManager.processedResults.value

        val total = processedFrames.size
        val (face, noFace) = processedFrames.fold(0 to 0) { acc, faceRecognitionResult ->
            when (faceRecognitionResult) {
                FaceRecognitionResult.FACE_DETECTED -> {
                    acc.copy(first = acc.first + 1)
                }

                FaceRecognitionResult.NO_FACE_DETECTED -> {
                    acc.copy(second = acc.second + 1)
                }

                else -> {
                    acc
                }
            }
        }

        _viewState.update {
            it.copy(
                sessionTotalDurationSeconds = total,
                faceDetectedDurationSeconds = face,
                noFaceDetectedDurationSeconds = noFace
            )
        }
    }
}
