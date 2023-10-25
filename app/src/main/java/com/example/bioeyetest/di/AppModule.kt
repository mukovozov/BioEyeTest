package com.example.bioeyetest.di

import com.example.bioeyetest.Navigator
import com.example.bioeyetest.NavigatorImpl
import com.example.bioeyetest.csv_generation.SessionCSVGenerator
import com.example.bioeyetest.csv_generation.SessionCSVGeneratorImpl
import com.example.bioeyetest.face_recognition.FaceRecognitionDataRepository
import com.example.bioeyetest.face_recognition.FaceRecognitionDataRepositoryImpl
import com.example.bioeyetest.face_recognition.FaceRecognitionDataUseCase
import com.example.bioeyetest.face_recognition.FaceRecognitionDataUseCaseImpl
import com.example.bioeyetest.face_recognition.FaceRecognitionProcessor
import com.example.bioeyetest.face_recognition.FaceRecognitionProcessorImpl
import com.example.bioeyetest.sensor.LightSensorProvider
import com.example.bioeyetest.sensor.LightSensorProviderImpl
import com.example.bioeyetest.utils.DispatchersProvider
import com.example.bioeyetest.utils.DispatchersProviderImpl
import com.example.bioeyetest.utils.FileManager
import com.example.bioeyetest.utils.FileManagerImpl
import com.example.bioeyetest.utils.TimeProvider
import com.example.bioeyetest.utils.TimeProviderImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindDispatchersProvider(dispatchersProviderImpl: DispatchersProviderImpl): DispatchersProvider

    @Binds
    @Singleton
    abstract fun bindNavigator(navigatorImpl: NavigatorImpl): Navigator

    @Binds
    abstract fun bindLightSensorProvider(lightSensorProviderImpl: LightSensorProviderImpl): LightSensorProvider

    @Binds
    @Singleton
    abstract fun bindFaceRecognitionManager(faceRecognitionManagerImpl: FaceRecognitionProcessorImpl): FaceRecognitionProcessor

    @Binds
    @Singleton
    abstract fun bindFaceRecognitionRepository(faceRecognitionDataRepositoryImpl: FaceRecognitionDataRepositoryImpl): FaceRecognitionDataRepository

    @Binds
    @Singleton
    abstract fun bindFaceRecognitionDataUseCase(faceRecognitionDataUseCaseImpl: FaceRecognitionDataUseCaseImpl): FaceRecognitionDataUseCase

    @Binds
    @Singleton
    abstract fun bindFileManager(fileManagerImpl: FileManagerImpl): FileManager

    @Binds
    @Singleton
    abstract fun bindTimeProvider(timeProviderImpl: TimeProviderImpl): TimeProvider

    @Binds
    abstract fun bindSessionSummaryCSVGenerator(csvGeneratorImpl: SessionCSVGeneratorImpl): SessionCSVGenerator
}