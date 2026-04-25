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
package com.bossmg.android.data.repository

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.bossmg.android.domain.enums.LanguageConfig
import com.bossmg.android.domain.repository.LanguageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class LanguageRepositoryImpl @Inject constructor() : LanguageRepository {
    private val _config =
        MutableStateFlow(
            AppCompatDelegate.getApplicationLocales().toLanguageConfig(),
        )

    override fun getLanguageConfig(): Flow<LanguageConfig> = _config

    override suspend fun setLanguageConfig(config: LanguageConfig) {
        _config.value = config
        withContext(Dispatchers.Main) {
            AppCompatDelegate.setApplicationLocales(config.toLocaleListCompat())
        }
    }
}

private fun LocaleListCompat.toLanguageConfig(): LanguageConfig {
    if (isEmpty) return LanguageConfig.FOLLOW_SYSTEM
    return when (get(0)?.language) {
        "ko" -> LanguageConfig.KOREAN
        "en" -> LanguageConfig.ENGLISH
        else -> LanguageConfig.FOLLOW_SYSTEM
    }
}

private fun LanguageConfig.toLocaleListCompat(): LocaleListCompat =
    when (this) {
        LanguageConfig.FOLLOW_SYSTEM -> LocaleListCompat.getEmptyLocaleList()
        LanguageConfig.KOREAN -> LocaleListCompat.forLanguageTags("ko")
        LanguageConfig.ENGLISH -> LocaleListCompat.forLanguageTags("en")
    }
