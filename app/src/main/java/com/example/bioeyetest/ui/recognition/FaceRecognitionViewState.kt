package com.example.bioeyetest.ui.recognition

sealed class FaceRecognitionViewState {
    data class Recognition(val detectionResult: UiFaceRecognitionResult) : FaceRecognitionViewState()
    object Preparing : FaceRecognitionViewState()

    data class PreparationFailed(val reason: PreparationFailedReason) : FaceRecognitionViewState()
}