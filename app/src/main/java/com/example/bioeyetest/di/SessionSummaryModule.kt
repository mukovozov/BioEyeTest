package com.example.bioeyetest.di

import com.example.bioeyetest.csv_generation.SessionCSVGenerator
import com.example.bioeyetest.csv_generation.SessionCSVGeneratorImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface SessionSummaryModule {
    @Binds
    fun bindSessionSummaryCSVGenerator(csvGeneratorImpl: SessionCSVGeneratorImpl): SessionCSVGenerator
}