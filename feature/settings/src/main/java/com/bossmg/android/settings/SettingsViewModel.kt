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
import com.bossmg.android.domain.model.User
import com.bossmg.android.domain.usecase.CancelSyncUseCase
import com.bossmg.android.domain.usecase.GetCurrentUserUseCase
import com.bossmg.android.domain.usecase.GetLanguageConfigUseCase
import com.bossmg.android.domain.usecase.GetThemeConfigUseCase
import com.bossmg.android.domain.usecase.ObserveSyncingStateUseCase
import com.bossmg.android.domain.usecase.RestoreFromCloudUseCase
import com.bossmg.android.domain.usecase.ScheduleImmediateSyncUseCase
import com.bossmg.android.domain.usecase.SetLanguageConfigUseCase
import com.bossmg.android.domain.usecase.SetThemeConfigUseCase
import com.bossmg.android.domain.usecase.SignInWithGoogleUseCase
import com.bossmg.android.domain.usecase.SignOutUseCase
import com.bossmg.android.domain.usecase.SyncNowUseCase
import com.bossmg.android.domain.usecase.SyncUploadUseCase
import com.bossmg.android.domain.usecase.base.invoke
import android.content.Context
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject

@HiltViewModel
internal class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getThemeConfigUseCase: GetThemeConfigUseCase,
    private val setThemeConfigUseCase: SetThemeConfigUseCase,
    private val getLanguageConfigUseCase: GetLanguageConfigUseCase,
    private val setLanguageConfigUseCase: SetLanguageConfigUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val restoreFromCloudUseCase: RestoreFromCloudUseCase,
    private val scheduleImmediateSyncUseCase: ScheduleImmediateSyncUseCase,
    private val syncUploadUseCase: SyncUploadUseCase,
    private val cancelSyncUseCase: CancelSyncUseCase,
    private val syncNowUseCase: SyncNowUseCase,
    private val observeSyncingStateUseCase: ObserveSyncingStateUseCase,
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

    val authUiState: StateFlow<AuthUiState> =
        getCurrentUserUseCase()
            .map<User?, AuthUiState> { user ->
                if (user != null) AuthUiState.SignedIn(user) else AuthUiState.Idle
            }
            .catch { emit(AuthUiState.Idle) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = AuthUiState.Idle,
            )

    private val _events = Channel<String>(Channel.BUFFERED)
    val events: Flow<String> = _events.receiveAsFlow()

    private val _isAuthLoading = MutableStateFlow(false)
    val isAuthLoading: StateFlow<Boolean> = _isAuthLoading.asStateFlow()

    private val _isManualSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> =
        combine(_isManualSyncing, observeSyncingStateUseCase()) { manual, worker -> manual || worker }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = false,
            )

    private val _showSignOutConfirm = MutableStateFlow(false)
    val showSignOutConfirm: StateFlow<Boolean> = _showSignOutConfirm.asStateFlow()

    fun onThemeSelect(config: ThemeConfig) {
        viewModelScope.launch {
            setThemeConfigUseCase(config)
        }
    }

    fun onLanguageSelect(config: LanguageConfig) {
        viewModelScope.launch {
            setLanguageConfigUseCase(config)
        }
    }

    fun onSignIn() {
        viewModelScope.launch {
            _isAuthLoading.value = true
            val user =
                signInWithGoogleUseCase().getOrElse { error ->
                    _isAuthLoading.value = false
                    _events.send(error.localizedMessage ?: context.getString(R.string.settings_sign_in_failed))
                    return@launch
                }
            val restoreResult = restoreFromCloudUseCase(user.uid)
            _isAuthLoading.value = false
            if (restoreResult.isFailure) {
                _events.send(restoreResult.exceptionOrNull()?.localizedMessage ?: context.getString(R.string.settings_restore_failed))
            } else {
                _events.send(context.getString(R.string.settings_sign_in_success))
            }
            scheduleImmediateSyncUseCase()
        }
    }

    fun onSignOut() {
        _showSignOutConfirm.value = true
    }

    fun onSignOutConfirmed() {
        _showSignOutConfirm.value = false
        viewModelScope.launch { performSignOut() }
    }

    fun onSignOutDismissed() {
        _showSignOutConfirm.value = false
    }

    private suspend fun performSignOut() {
        _isAuthLoading.value = true
        val uploadResult = withTimeoutOrNull(10_000L) { syncUploadUseCase() }
        if (uploadResult == null || uploadResult.isFailure) {
            _isAuthLoading.value = false
            _events.send(context.getString(R.string.settings_sign_out_sync_failed))
            return
        }
        cancelSyncUseCase()
        signOutUseCase()
            .onSuccess {
                _isAuthLoading.value = false
                _events.send(context.getString(R.string.settings_sign_out_success))
            }
            .onFailure { error ->
                _isAuthLoading.value = false
                _events.send(error.localizedMessage ?: context.getString(R.string.settings_sign_out_failed))
            }
    }

    fun onSyncNow() {
        viewModelScope.launch {
            _isManualSyncing.value = true
            syncNowUseCase()
                .onSuccess { _events.send(context.getString(R.string.settings_sync_success)) }
                .onFailure { _events.send(it.localizedMessage ?: context.getString(R.string.settings_sync_failed)) }
            _isManualSyncing.value = false
        }
    }
}
