package com.example.bioeyetest.di

import com.example.bioeyetest.ui.Navigator
import com.example.bioeyetest.ui.NavigatorImpl
import com.example.bioeyetest.core.coroutines.DispatchersProvider
import com.example.bioeyetest.core.coroutines.DispatchersProviderImpl
import com.example.bioeyetest.core.file.FileManager
import com.example.bioeyetest.core.file.FileManagerImpl
import com.example.bioeyetest.core.date_time.TimeProvider
import com.example.bioeyetest.core.date_time.TimeProviderImpl
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