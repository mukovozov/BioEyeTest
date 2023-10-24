package com.example.bioeyetest.ui.session_summary

import android.net.Uri

sealed class SessionSummaryEvents {
    data class ShareCSV(val contentUri: Uri) : SessionSummaryEvents()
}