package com.bossmg.android.data.di

import android.content.Context
import com.bossmg.android.fts.NativeSearchEngine
import com.bossmg.android.fts.SearchEngine
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object NativeModule {
    @Provides
    @Singleton
    fun providesNativeSearchEngine(
        @ApplicationContext context: Context,
    ): SearchEngine {
        val indexPath = context.filesDir.resolve("fts_index").absolutePath
        return NativeSearchEngine(indexPath)
    }

    @Provides
    @Singleton
    @ApplicationScope
    fun providesApplicationScope(): CoroutineScope =
        CoroutineScope(SupervisorJob() + Dispatchers.Default)
}
