package com.bossmg.android.data.di

import com.bossmg.android.data.database.LifeLogDao
import com.bossmg.android.data.database.LifeLogDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DaoModule {
    @Provides
    @Singleton
    fun providesLifeLogDao(
        database: LifeLogDatabase
    ): LifeLogDao = database.lifeLogDao()
}