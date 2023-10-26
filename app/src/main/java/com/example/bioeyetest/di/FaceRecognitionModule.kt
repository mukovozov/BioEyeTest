package com.example.bioeyetest.di

import com.example.bioeyetest.data.face_recognition.FaceRecognitionDataRepository
import com.example.bioeyetest.data.face_recognition.FaceRecognitionDataRepositoryImpl
import com.example.bioeyetest.domain.face_recognition.FaceRecognitionProcessor
import com.example.bioeyetest.domain.face_recognition.FaceRecognitionProcessorImpl
import com.example.bioeyetest.data.sensor.LightSensorManager
import com.example.bioeyetest.data.sensor.LightSensorManagerImpl
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
    fun bindLightSensorManager(lightSensorManagerImpl: LightSensorManagerImpl): LightSensorManager

    @Binds
    @Singleton
    fun bindFaceRecognitionProcessor(faceRecognitionProcessorImpl: FaceRecognitionProcessorImpl): FaceRecognitionProcessor

    @Binds
    @Singleton
    fun bindFaceRecognitionRepository(faceRecognitionDataRepositoryImpl: FaceRecognitionDataRepositoryImpl): FaceRecognitionDataRepository

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