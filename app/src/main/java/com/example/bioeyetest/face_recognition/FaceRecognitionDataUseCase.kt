package com.example.bioeyetest.face_recognition

import javax.inject.Inject

interface FaceRecognitionDataUseCase {
    suspend fun getAll(): List<FaceRecognitionData>

    suspend fun clear()
}

class FaceRecognitionDataUseCaseImpl @Inject constructor(
    private val faceRecognitionDataRepository: FaceRecognitionDataRepository,
) : FaceRecognitionDataUseCase {
    override suspend fun getAll(): List<FaceRecognitionData> {
        return faceRecognitionDataRepository.getAll()
    }

    override suspend fun clear() {
        faceRecognitionDataRepository.clear()
    }
}