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
package com.bossmg.android.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bossmg.android.domain.enums.LanguageConfig
import com.bossmg.android.domain.enums.ThemeConfig
import com.bossmg.android.domain.usecase.GetLanguageConfigUseCase
import com.bossmg.android.domain.usecase.GetThemeConfigUseCase
import com.bossmg.android.domain.usecase.SetLanguageConfigUseCase
import com.bossmg.android.domain.usecase.SetThemeConfigUseCase
import com.bossmg.android.domain.usecase.base.invoke
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class SettingsViewModel @Inject constructor(
    private val getThemeConfigUseCase: GetThemeConfigUseCase,
    private val setThemeConfigUseCase: SetThemeConfigUseCase,
    private val getLanguageConfigUseCase: GetLanguageConfigUseCase,
    private val setLanguageConfigUseCase: SetLanguageConfigUseCase,
) : ViewModel() {
    val themeConfig: StateFlow<ThemeConfig> =
        getThemeConfigUseCase()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = ThemeConfig.FOLLOW_SYSTEM,
            )

    val languageConfig: StateFlow<LanguageConfig> =
        getLanguageConfigUseCase()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = LanguageConfig.FOLLOW_SYSTEM,
            )

    fun onThemeSelect(config: ThemeConfig) {
        viewModelScope.launch {
            setThemeConfigUseCase(config)
        }
    }

    suspend fun onLanguageSelect(config: LanguageConfig) {
        setLanguageConfigUseCase(config)
    }
}
