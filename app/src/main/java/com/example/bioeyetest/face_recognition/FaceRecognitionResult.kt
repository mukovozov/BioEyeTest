package com.example.bioeyetest.face_recognition

enum class FaceRecognitionResult(val binaryValue: Int) {
    NO_RESULT(-1),
    FACE_DETECTED(1),
    NO_FACE_DETECTED(0)
}