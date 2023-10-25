package com.example.bioeyetest.di

import com.example.bioeyetest.ui.Navigator
import com.example.bioeyetest.ui.NavigatorImpl
import com.example.bioeyetest.core.DispatchersProvider
import com.example.bioeyetest.core.DispatchersProviderImpl
import com.example.bioeyetest.core.FileManager
import com.example.bioeyetest.core.FileManagerImpl
import com.example.bioeyetest.core.TimeProvider
import com.example.bioeyetest.core.TimeProviderImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface AppModule {

    @Binds
    @Singleton
    fun bindDispatchersProvider(dispatchersProviderImpl: DispatchersProviderImpl): DispatchersProvider

    @Binds
    @Singleton
    fun bindNavigator(navigatorImpl: NavigatorImpl): Navigator

    @Binds
    @Singleton
    fun bindFileManager(fileManagerImpl: FileManagerImpl): FileManager

    @Binds
    @Singleton
    fun bindTimeProvider(timeProviderImpl: TimeProviderImpl): TimeProvider
}