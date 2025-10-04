package com.bossmg.android.data.di

import android.content.Context
import androidx.room.Room
import com.bossmg.android.data.database.LifeLogDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {
    @Provides
    @Singleton
    fun providesLifeLogDatabase(
        @ApplicationContext context: Context
    ): LifeLogDatabase = Room.databaseBuilder(
        context,
        LifeLogDatabase::class.java,
        "life-log-database"
    ).build()
}