package com.bossmg.android.settings

import com.bossmg.android.domain.model.User

sealed interface AuthUiState {
    data object Idle : AuthUiState

    data class SignedIn(val user: User) : AuthUiState
}
