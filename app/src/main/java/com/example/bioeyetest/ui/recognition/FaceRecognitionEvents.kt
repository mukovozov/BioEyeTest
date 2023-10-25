package com.example.bioeyetest.ui.recognition

sealed class FaceRecognitionEvents {
    object ProvideNextFrame : FaceRecognitionEvents()
}