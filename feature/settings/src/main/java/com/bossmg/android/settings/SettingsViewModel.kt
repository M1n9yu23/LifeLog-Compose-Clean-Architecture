package com.bossmg.android.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bossmg.android.domain.enums.LanguageConfig
import com.bossmg.android.domain.enums.ThemeConfig
import com.bossmg.android.domain.model.User
import com.bossmg.android.domain.usecase.GetCurrentUserUseCase
import com.bossmg.android.domain.usecase.GetLanguageConfigUseCase
import com.bossmg.android.domain.usecase.GetThemeConfigUseCase
import com.bossmg.android.domain.usecase.SetLanguageConfigUseCase
import com.bossmg.android.domain.usecase.SetThemeConfigUseCase
import com.bossmg.android.domain.usecase.SignInWithGoogleUseCase
import com.bossmg.android.domain.usecase.SignOutUseCase
import com.bossmg.android.domain.usecase.base.invoke
import com.bossmg.android.sync.SyncManager
import dagger.hilt.android.lifecycle.HiltViewModel
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

sealed interface AuthUiState {
    data object Idle : AuthUiState

    data class SignedIn(val user: User) : AuthUiState
}

@HiltViewModel
internal class SettingsViewModel @Inject constructor(
    private val getThemeConfigUseCase: GetThemeConfigUseCase,
    private val setThemeConfigUseCase: SetThemeConfigUseCase,
    private val getLanguageConfigUseCase: GetLanguageConfigUseCase,
    private val setLanguageConfigUseCase: SetLanguageConfigUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val syncManager: SyncManager,
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
        combine(_isManualSyncing, syncManager.isSyncWorkerRunning) { manual, worker -> manual || worker }
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
                    _events.send(error.localizedMessage ?: "로그인 실패")
                    return@launch
                }
            val restoreResult = syncManager.restoreFromCloud(user.uid)
            _isAuthLoading.value = false
            if (restoreResult.isFailure) {
                _events.send(restoreResult.exceptionOrNull()?.localizedMessage ?: "데이터 복원 실패")
            } else {
                _events.send("로그인되었습니다")
            }
            syncManager.scheduleImmediateSync()
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
        withTimeoutOrNull(10_000L) { syncManager.syncUpload() }
        syncManager.cancelAllWork()
        signOutUseCase()
            .onSuccess {
                _isAuthLoading.value = false
                _events.send("로그아웃되었습니다")
            }
            .onFailure { error ->
                _isAuthLoading.value = false
                _events.send(error.localizedMessage ?: "로그아웃 실패")
            }
    }

    fun onSyncNow() {
        viewModelScope.launch {
            _isManualSyncing.value = true
            syncManager.syncNow()
                .onSuccess { _events.send("동기화 완료") }
                .onFailure { _events.send(it.localizedMessage ?: "동기화 실패") }
            _isManualSyncing.value = false
        }
    }
}
