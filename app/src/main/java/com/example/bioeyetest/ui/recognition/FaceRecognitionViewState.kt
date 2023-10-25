package com.example.bioeyetest.ui.recognition

import com.example.bioeyetest.data.face_recognition.FaceRecognitionResult

sealed class FaceRecognitionViewState {
    data class Recognition(val detectionResult: FaceRecognitionResult) : FaceRecognitionViewState()
    object Preparing : FaceRecognitionViewState()

    data class PreparationFailed(val reason: PreparationFailedReason) : FaceRecognitionViewState()
}