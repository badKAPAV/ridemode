package com.kapav.ridemode.core.di

import android.content.Context
import com.kapav.ridemode.data.RideRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RideModule {
    @Provides
    @Singleton
    fun repository(@ApplicationContext context: Context): RideRepository = RideRepository.get(context)
}
