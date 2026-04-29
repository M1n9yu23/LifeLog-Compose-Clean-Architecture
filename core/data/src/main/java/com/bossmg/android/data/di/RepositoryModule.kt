/*
 * Copyright 2026 Gyugle
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bossmg.android.data.di

import com.bossmg.android.data.datasource.SyncDataSource
import com.bossmg.android.data.datasource.SyncDataSourceImpl
import com.bossmg.android.data.repository.LanguageRepositoryImpl
import com.bossmg.android.data.repository.LifeLogRepositoryImpl
import com.bossmg.android.data.repository.ThemeRepositoryImpl
import com.bossmg.android.domain.repository.LanguageRepository
import com.bossmg.android.domain.repository.LifeLogReadRepository
import com.bossmg.android.domain.repository.LifeLogSearchRepository
import com.bossmg.android.domain.repository.LifeLogWriteRepository
import com.bossmg.android.domain.repository.ThemeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindsLifeLogReadRepository(impl: LifeLogRepositoryImpl): LifeLogReadRepository

    @Binds
    @Singleton
    abstract fun bindsLifeLogWriteRepository(impl: LifeLogRepositoryImpl): LifeLogWriteRepository

    @Binds
    @Singleton
    abstract fun bindsLifeLogSearchRepository(impl: LifeLogRepositoryImpl): LifeLogSearchRepository

    @Binds
    @Singleton
    abstract fun bindsThemeRepository(impl: ThemeRepositoryImpl): ThemeRepository

    @Binds
    @Singleton
    abstract fun bindsLanguageRepository(impl: LanguageRepositoryImpl): LanguageRepository

    @Binds
    @Singleton
    abstract fun bindsSyncDataSource(impl: SyncDataSourceImpl): SyncDataSource
}
