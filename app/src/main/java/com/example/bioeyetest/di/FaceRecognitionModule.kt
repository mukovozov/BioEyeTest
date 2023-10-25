package com.example.bioeyetest.di

import com.example.bioeyetest.face_recognition.FaceRecognitionDataRepository
import com.example.bioeyetest.face_recognition.FaceRecognitionDataRepositoryImpl
import com.example.bioeyetest.face_recognition.FaceRecognitionDataUseCase
import com.example.bioeyetest.face_recognition.FaceRecognitionDataUseCaseImpl
import com.example.bioeyetest.face_recognition.FaceRecognitionProcessor
import com.example.bioeyetest.face_recognition.FaceRecognitionProcessorImpl
import com.example.bioeyetest.sensor.LightSensorManager
import com.example.bioeyetest.sensor.LightSensorManagerImpl
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface FaceRecognitionModule {

    @Binds
    abstract fun bindLightSensorManager(lightSensorManagerImpl: LightSensorManagerImpl): LightSensorManager

    @Binds
    @Singleton
    fun bindFaceRecognitionProcessor(faceRecognitionProcessorImpl: FaceRecognitionProcessorImpl): FaceRecognitionProcessor

    @Binds
    @Singleton
    fun bindFaceRecognitionRepository(faceRecognitionDataRepositoryImpl: FaceRecognitionDataRepositoryImpl): FaceRecognitionDataRepository

    @Binds
    @Singleton
    fun bindFaceRecognitionDataUseCase(faceRecognitionDataUseCaseImpl: FaceRecognitionDataUseCaseImpl): FaceRecognitionDataUseCase

    companion object {
        @Provides
        @Singleton
        fun provideFaceDetector(): FaceDetector {
            val options = FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
                .build()

            return FaceDetection.getClient(options)
        }
    }
}