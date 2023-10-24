package com.example.bioeyetest.di

import com.example.bioeyetest.Navigator
import com.example.bioeyetest.NavigatorImpl
import com.example.bioeyetest.csv_generation.SessionCSVGenerator
import com.example.bioeyetest.csv_generation.SessionCSVGeneratorImpl
import com.example.bioeyetest.face_recognition.FaceRecognitionManager
import com.example.bioeyetest.face_recognition.FaceRecognitionManagerImpl
import com.example.bioeyetest.sensor.LightSensorProvider
import com.example.bioeyetest.sensor.LightSensorProviderImpl
import com.example.bioeyetest.utils.FileManager
import com.example.bioeyetest.utils.FileManagerImpl
import com.example.bioeyetest.utils.TimeProvider
import com.example.bioeyetest.utils.TimeProviderImpl
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindNavigator(navigatorImpl: NavigatorImpl): Navigator

    @Binds
    abstract fun bindLightSensorProvider(lightSensorProviderImpl: LightSensorProviderImpl): LightSensorProvider

    @Binds
    @Singleton
    abstract fun bindFaceRecognitionManager(faceRecognitionManagerImpl: FaceRecognitionManagerImpl): FaceRecognitionManager

    @Binds
    @Singleton
    abstract fun bindFileManager(fileManagerImpl: FileManagerImpl): FileManager

    @Binds
    @Singleton
    abstract fun bindTimeProvider(timeProviderImpl: TimeProviderImpl): TimeProvider

    @Binds
    abstract fun bindSessionSummaryCSVGenerator(csvGeneratorImpl: SessionCSVGeneratorImpl): SessionCSVGenerator
}