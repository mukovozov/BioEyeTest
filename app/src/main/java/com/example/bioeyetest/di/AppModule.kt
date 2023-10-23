package com.example.bioeyetest.di

import com.example.bioeyetest.Navigator
import com.example.bioeyetest.NavigatorImpl
import com.example.bioeyetest.sensor.LightSensorProvider
import com.example.bioeyetest.sensor.LightSensorProviderImpl
import dagger.Binds
import dagger.Module
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
}