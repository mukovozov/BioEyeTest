package com.example.bioeyetest.ui.recognition

import com.example.bioeyetest.ui.recognition.models.PreparationFailedReason
import com.example.bioeyetest.ui.recognition.models.UiFaceRecognitionResult

sealed class FaceRecognitionViewState {
    data class Recognition(val detectionResult: UiFaceRecognitionResult) : FaceRecognitionViewState()

    object Preparing : FaceRecognitionViewState()

    data class PreparationFailed(val reason: PreparationFailedReason) : FaceRecognitionViewState()
}