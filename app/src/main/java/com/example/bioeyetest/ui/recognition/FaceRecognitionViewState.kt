package com.example.bioeyetest.ui.recognition

sealed class FaceRecognitionViewState {
    object Recognition : FaceRecognitionViewState()
    object Preparing : FaceRecognitionViewState()

    data class PreparationFailed(val reason: PreparationFailedReason) : FaceRecognitionViewState()
}