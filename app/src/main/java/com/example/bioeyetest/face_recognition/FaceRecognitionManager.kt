package com.example.bioeyetest.face_recognition

import android.graphics.Bitmap
import android.util.Log
import com.example.bioeyetest.utils.TimeProvider
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

interface FaceRecognitionManager {
    val processedResults: StateFlow<List<FaceRecognitionData>>
    suspend fun process(bitmap: Bitmap): Result<FaceRecognitionResult>

    // TODO: do here only processing, create repository and save data per session there. create session id on welcome screen and pass it through screens.
    fun clear()
}

class FaceRecognitionManagerImpl @Inject constructor(
    private val faceDetector: FaceDetector,
    private val timeProvider: TimeProvider,
) : FaceRecognitionManager {

    override val processedResults = MutableStateFlow<List<FaceRecognitionData>>(emptyList())

    override suspend fun process(bitmap: Bitmap): Result<FaceRecognitionResult> {
        return suspendCoroutine { continuation ->
            val inputImage = InputImage.fromBitmap(bitmap, 0)

            faceDetector.process(inputImage)
                .addOnSuccessListener { faces ->
                    val recognitionResult = if (faces.isEmpty()) {
                        FaceRecognitionResult.NO_FACE_DETECTED
                    } else {
                        FaceRecognitionResult.FACE_DETECTED
                    }

                    processedResults.update {
                        it.plus(FaceRecognitionData(recognitionResult, timeProvider.currentTimeMillis))
                    }

                    continuation.resume(Result.success(recognitionResult))
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "failed to process ${exception.message}", exception)
                    continuation.resume(Result.failure(exception))
                }
        }
    }

    override fun clear() {
        processedResults.value = emptyList()
    }

    private companion object {
        const val TAG = "FaceRecognitionManager"
    }
}