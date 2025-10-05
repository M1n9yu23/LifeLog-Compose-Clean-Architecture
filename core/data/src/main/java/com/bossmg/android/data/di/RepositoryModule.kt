package com.bossmg.android.data.di

import com.bossmg.android.data.repository.LifeLogRepositoryImpl
import com.bossmg.android.domain.repository.LifeLogRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindsLifeLogRepository(
        lifeLogRepositoryImpl: LifeLogRepositoryImpl
    ): LifeLogRepository
}