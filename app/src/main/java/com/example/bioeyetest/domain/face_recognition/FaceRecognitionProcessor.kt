package com.example.bioeyetest.domain.face_recognition

import android.graphics.Bitmap
import android.util.Log
import com.example.bioeyetest.data.face_recognition.FaceRecognitionData
import com.example.bioeyetest.data.face_recognition.FaceRecognitionDataRepository
import com.example.bioeyetest.data.face_recognition.FaceRecognitionResult
import com.example.bioeyetest.core.DispatchersProvider
import com.example.bioeyetest.core.TimeProvider
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetector
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

interface FaceRecognitionProcessor {
    suspend fun process(bitmap: Bitmap): Result<FaceRecognitionResult>
}

class FaceRecognitionProcessorImpl @Inject constructor(
    private val faceRecognitionDataRepository: FaceRecognitionDataRepository,
    private val faceDetector: FaceDetector,
    private val timeProvider: TimeProvider,
    private val dispatchersProvider: DispatchersProvider,
) : FaceRecognitionProcessor {

    override suspend fun process(bitmap: Bitmap): Result<FaceRecognitionResult> {
        return withContext(dispatchersProvider.default) {
            processImpl(bitmap).onSuccess { recognitionResult ->
                faceRecognitionDataRepository.save(
                    FaceRecognitionData(recognitionResult, timeProvider.currentTimeMillis)
                )
            }
        }
    }

    private suspend fun processImpl(bitmap: Bitmap): Result<FaceRecognitionResult> {
        return suspendCoroutine { continuation ->
            val inputImage = InputImage.fromBitmap(bitmap, 0)

            faceDetector.process(inputImage)
                .addOnSuccessListener { faces ->
                    val recognitionResult = if (faces.isEmpty()) {
                        FaceRecognitionResult.NO_FACE_DETECTED
                    } else {
                        FaceRecognitionResult.FACE_DETECTED
                    }

                    continuation.resume(Result.success(recognitionResult))
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "failed to process ${exception.message}", exception)
                    continuation.resume(Result.failure(exception))
                }
        }
    }

    private companion object {
        const val TAG = "FaceRecognitionManager"
    }
}