package com.example.bioeyetest.ui.session_summary

import android.annotation.SuppressLint
import android.content.Context
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bioeyetest.ui.Navigator
import com.example.bioeyetest.R
import com.example.bioeyetest.domain.csv_generation.SessionCSVGenerator
import com.example.bioeyetest.data.face_recognition.FaceRecognitionResult
import com.example.bioeyetest.core.date_time.TimeProvider
import com.example.bioeyetest.core.date_time.format
import com.example.bioeyetest.data.face_recognition.FaceRecognitionDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@SuppressLint("StaticFieldLeak")
class SessionSummaryViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val faceRecognitionDataRepository: FaceRecognitionDataRepository,
    private val sessionSummaryCsvGenerator: SessionCSVGenerator,
    private val timeProvider: TimeProvider,
    private val navigator: Navigator,
) : ViewModel() {

    val viewState: StateFlow<SessionSummaryViewState>
        get() = _viewState
    private val _viewState = MutableStateFlow(SessionSummaryViewState())

    val events: SharedFlow<SessionSummaryEvents>
        get() = _events
    private val _events = MutableSharedFlow<SessionSummaryEvents>(extraBufferCapacity = 1)

    init {
        viewModelScope.launch {

            val processedFrames = faceRecognitionDataRepository.getAll()

            val total = processedFrames.size
            val (face, noFace) = processedFrames.fold(0 to 0) { acc, faceRecognitionData ->
                when (faceRecognitionData.result) {
                    FaceRecognitionResult.FACE_DETECTED -> {
                        acc.copy(first = acc.first + 1)
                    }

                    FaceRecognitionResult.NO_FACE_DETECTED -> {
                        acc.copy(second = acc.second + 1)
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

    fun onShareButtonClicked() {
        viewModelScope.launch {
            val fileName = "$CSV_REPORT_FILE_PREFIX${timeProvider.currentTimeMillis.format(CSV_REPORT_DATE_FORMAT)}"
            val frames = faceRecognitionDataRepository.getAll()
            sessionSummaryCsvGenerator.generateCSV(frames, fileName)
                .onSuccess { csv ->
                    val contentUri = FileProvider.getUriForFile(
                        context,
                        FILE_PROVIDER_PATH,
                        csv
                    )

                    _events.emit(SessionSummaryEvents.ShareCSV(contentUri))
                }
                .onFailure {
                    val message = context.getString(R.string.session_summary_generate_csv_error_message)
                    _events.emit(SessionSummaryEvents.ShowMessage(message))
                }
        }
    }

    fun onBackPressed() {
        cleanUpAndGoToWelcomeScreen()
    }

    fun onDoneButtonClicked() {
        cleanUpAndGoToWelcomeScreen()
    }

    private fun cleanUpAndGoToWelcomeScreen() {
        viewModelScope.launch {
            faceRecognitionDataRepository.clear()

            navigator.navigateTo(R.id.action_summaryFragment_to_welcomeFragment)
        }
    }

    private companion object {
        const val TAG = "SessionSummaryViewModel"
        const val CSV_REPORT_DATE_FORMAT = "yyyy-MM-dd_HH:mm:ss"
        const val CSV_REPORT_FILE_PREFIX = "bioeye-"
        const val FILE_PROVIDER_PATH = "com.example.bioeyetest.utils.BioEyeTestFileProvider"
    }
}
