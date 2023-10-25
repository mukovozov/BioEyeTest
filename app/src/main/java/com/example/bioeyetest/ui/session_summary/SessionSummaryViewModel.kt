package com.example.bioeyetest.ui.session_summary

import android.annotation.SuppressLint
import android.content.Context
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bioeyetest.Navigator
import com.example.bioeyetest.R
import com.example.bioeyetest.csv_generation.SessionCSVGenerator
import com.example.bioeyetest.face_recognition.FaceRecognitionDataUseCase
import com.example.bioeyetest.face_recognition.FaceRecognitionProcessor
import com.example.bioeyetest.face_recognition.FaceRecognitionResult
import com.example.bioeyetest.utils.TimeProvider
import com.example.bioeyetest.utils.toIso8601
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
    private val faceRecognitionDataUseCase: FaceRecognitionDataUseCase,
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

            val processedFrames = faceRecognitionDataUseCase.getAll()

            val total = processedFrames.size
            val (face, noFace) = processedFrames.fold(0 to 0) { acc, faceRecognitionData ->
                when (faceRecognitionData.result) {
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

    fun onShareButtonClicked() {
        viewModelScope.launch {
            val fileName = "$CSV_REPORT_FILE_PREFIX${timeProvider.currentTimeMillis.toIso8601()}"
            val frames = faceRecognitionDataUseCase.getAll()
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
                    // TODO: send toast event
                }
        }
    }

    fun onDoneButtonClicked() {
        viewModelScope.launch {
            faceRecognitionDataUseCase.clear()

            navigator.navigateTo(R.id.action_summaryFragment_to_welcomeFragment)
        }
    }

    private companion object {
        const val TAG = "SessionSummaryViewModel"
        const val CSV_REPORT_FILE_PREFIX = "bioeye-"
        const val FILE_PROVIDER_PATH = "com.example.bioeyetest.utils.BioEyeTestFileProvider"
    }
}
