package com.example.bioeyetest.data.face_recognition

import com.example.bioeyetest.core.coroutines.DispatchersProvider
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface FaceRecognitionDataRepository {

    suspend fun save(data: FaceRecognitionData)

    suspend fun getAll(): List<FaceRecognitionData>

    suspend fun clear()
}

internal class FaceRecognitionDataRepositoryImpl @Inject constructor(
    private val dispatchersProvider: DispatchersProvider
) : FaceRecognitionDataRepository {

    private val cache: MutableList<FaceRecognitionData> = mutableListOf()

    // Needed to guarantee thread safety of the modified collection
    private val mutex = Mutex()

    override suspend fun save(data: FaceRecognitionData) {
        withContext(dispatchersProvider.io) {
            mutex.withLock {
                cache.add(data)
            }
        }
    }

    override suspend fun getAll(): List<FaceRecognitionData> {
        return cache.toList()
    }

    override suspend fun clear() {
        withContext(dispatchersProvider.io) {
            mutex.withLock {
                cache.clear()
            }
        }
    }
}