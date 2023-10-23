package com.example.bioeyetest.ui.session_summary

data class SessionSummaryViewState(
    val sessionTotalDurationSeconds: Int = 0,
    val faceDetectedDurationSeconds: Int = 0,
    val noFaceDetectedDurationSeconds: Int = 0,
)