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

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.bossmg.android.designsystem.ui.components.ConfirmDialog
import com.bossmg.android.designsystem.ui.icons.LifeIcons
import com.bossmg.android.designsystem.ui.theme.AppTypography
import com.bossmg.android.designsystem.ui.theme.DP12
import com.bossmg.android.designsystem.ui.theme.DP16
import com.bossmg.android.designsystem.ui.theme.DP2
import com.bossmg.android.designsystem.ui.theme.DP24
import com.bossmg.android.designsystem.ui.theme.DP4
import com.bossmg.android.designsystem.ui.theme.DP8
import com.bossmg.android.designsystem.ui.theme.DarkBackground
import com.bossmg.android.designsystem.ui.theme.DarkPrimary
import com.bossmg.android.designsystem.ui.theme.DarkSurface
import com.bossmg.android.designsystem.ui.theme.LightBackground
import com.bossmg.android.designsystem.ui.theme.LightPrimary
import com.bossmg.android.designsystem.ui.theme.LightSurface
import com.bossmg.android.domain.enums.LanguageConfig
import com.bossmg.android.domain.enums.ThemeConfig

@Composable
internal fun Settings(
    onBack: () -> Unit,
    onRestartRequired: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val themeConfig by viewModel.themeConfig.collectAsStateWithLifecycle()
    val languageConfig by viewModel.languageConfig.collectAsStateWithLifecycle()
    val authUiState by viewModel.authUiState.collectAsStateWithLifecycle()
    val isAuthLoading by viewModel.isAuthLoading.collectAsStateWithLifecycle()
    val isSyncing by viewModel.isSyncing.collectAsStateWithLifecycle()
    val showSignOutConfirm by viewModel.showSignOutConfirm.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    val signInSuccess = stringResource(R.string.settings_sign_in_success)
    val signInFailedFallback = stringResource(R.string.settings_sign_in_failed)
    val restoreFailedFallback = stringResource(R.string.settings_restore_failed)
    val signOutSuccess = stringResource(R.string.settings_sign_out_success)
    val signOutFailedFallback = stringResource(R.string.settings_sign_out_failed)
    val signOutSyncFailed = stringResource(R.string.settings_sign_out_sync_failed)
    val syncSuccess = stringResource(R.string.settings_sync_success)
    val syncFailedFallback = stringResource(R.string.settings_sync_failed)

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(Unit) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.events.collect { event ->
                val message =
                    when (event) {
                        SettingsEvent.SignInSuccess -> signInSuccess
                        is SettingsEvent.SignInFailed -> event.cause?.localizedMessage ?: signInFailedFallback
                        is SettingsEvent.RestoreFailed -> event.cause?.localizedMessage ?: restoreFailedFallback
                        SettingsEvent.SignOutSuccess -> signOutSuccess
                        is SettingsEvent.SignOutFailed -> event.cause?.localizedMessage ?: signOutFailedFallback
                        SettingsEvent.SignOutSyncFailed -> signOutSyncFailed
                        SettingsEvent.SyncSuccess -> syncSuccess
                        is SettingsEvent.SyncFailed -> event.cause?.localizedMessage ?: syncFailedFallback
                    }
                snackbarHostState.showSnackbar(
                    message = message,
                    duration = SnackbarDuration.Short,
                )
            }
        }
    }

    SettingsScreen(
        themeConfig = themeConfig,
        onThemeSelect = viewModel::onThemeSelect,
        languageConfig = languageConfig,
        onLanguageSelect = { config ->
            if (config != languageConfig) {
                viewModel.onLanguageSelect(config)
            }
        },
        authUiState = authUiState,
        isAuthLoading = isAuthLoading,
        isSyncing = isSyncing,
        onSignIn = viewModel::onSignIn,
        onSignOut = viewModel::onSignOut,
        onSignOutConfirmed = viewModel::onSignOutConfirmed,
        onSignOutDismissed = viewModel::onSignOutDismissed,
        showSignOutConfirm = showSignOutConfirm,
        onSyncNow = viewModel::onSyncNow,
        snackbarHostState = snackbarHostState,
        onBack = onBack,
    )
}

@Composable
private fun SettingsScreen(
    themeConfig: ThemeConfig,
    onThemeSelect: (ThemeConfig) -> Unit,
    languageConfig: LanguageConfig,
    onLanguageSelect: (LanguageConfig) -> Unit,
    authUiState: AuthUiState,
    isAuthLoading: Boolean,
    isSyncing: Boolean,
    onSignIn: () -> Unit,
    onSignOut: () -> Unit,
    onSignOutConfirmed: () -> Unit,
    onSignOutDismissed: () -> Unit,
    showSignOutConfirm: Boolean,
    onSyncNow: () -> Unit,
    snackbarHostState: SnackbarHostState,
    onBack: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .statusBarsPadding(),
        ) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = DP4, vertical = DP8),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = LifeIcons.ArrowLeft,
                        contentDescription = stringResource(R.string.cd_back),
                        tint = MaterialTheme.colorScheme.onBackground,
                    )
                }
                Spacer(modifier = Modifier.width(DP8))
                Text(
                    text = stringResource(R.string.settings_title),
                    style = AppTypography.titleLarge.copy(color = MaterialTheme.colorScheme.onBackground),
                )
            }

            Column(modifier = Modifier.padding(horizontal = DP16)) {
                Spacer(modifier = Modifier.height(DP16))

                Text(
                    text = stringResource(R.string.settings_theme_section),
                    style = AppTypography.bodyMedium.copy(color = MaterialTheme.colorScheme.primary),
                )

                Spacer(modifier = Modifier.height(DP12))

                ThemeOptionItem(
                    label = stringResource(R.string.settings_theme_system),
                    selected = themeConfig == ThemeConfig.FOLLOW_SYSTEM,
                    onClick = { onThemeSelect(ThemeConfig.FOLLOW_SYSTEM) },
                    previewDark = null,
                )

                ThemeOptionItem(
                    label = stringResource(R.string.settings_theme_light),
                    selected = themeConfig == ThemeConfig.LIGHT,
                    onClick = { onThemeSelect(ThemeConfig.LIGHT) },
                    previewDark = false,
                )

                ThemeOptionItem(
                    label = stringResource(R.string.settings_theme_dark),
                    selected = themeConfig == ThemeConfig.DARK,
                    onClick = { onThemeSelect(ThemeConfig.DARK) },
                    previewDark = true,
                )

                Spacer(modifier = Modifier.height(DP16))

                Text(
                    text = stringResource(R.string.settings_language_section),
                    style = AppTypography.bodyMedium.copy(color = MaterialTheme.colorScheme.primary),
                )

                Spacer(modifier = Modifier.height(DP12))

                LanguageOptionItem(
                    label = stringResource(R.string.settings_language_system),
                    selected = languageConfig == LanguageConfig.FOLLOW_SYSTEM,
                    onClick = { onLanguageSelect(LanguageConfig.FOLLOW_SYSTEM) },
                )

                LanguageOptionItem(
                    label = stringResource(R.string.settings_language_korean),
                    selected = languageConfig == LanguageConfig.KOREAN,
                    onClick = { onLanguageSelect(LanguageConfig.KOREAN) },
                )

                LanguageOptionItem(
                    label = stringResource(R.string.settings_language_english),
                    selected = languageConfig == LanguageConfig.ENGLISH,
                    onClick = { onLanguageSelect(LanguageConfig.ENGLISH) },
                )

                Spacer(modifier = Modifier.height(DP16))

                Text(
                    text = stringResource(R.string.settings_account_section),
                    style = AppTypography.bodyMedium.copy(color = MaterialTheme.colorScheme.primary),
                )

                Spacer(modifier = Modifier.height(DP12))

                AccountSection(
                    authUiState = authUiState,
                    isAuthLoading = isAuthLoading,
                    isSyncing = isSyncing,
                    onSignIn = onSignIn,
                    onSignOut = onSignOut,
                    onSyncNow = onSyncNow,
                )
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter),
        )

        if (showSignOutConfirm) {
            ConfirmDialog(
                title = stringResource(R.string.settings_sign_out),
                message = stringResource(R.string.settings_sign_out_confirm_message),
                confirmText = stringResource(R.string.settings_sign_out),
                dismissText = stringResource(R.string.settings_language_cancel),
                onConfirm = onSignOutConfirmed,
                onDismiss = onSignOutDismissed,
            )
        }
    }
}

@Composable
private fun AccountSection(
    authUiState: AuthUiState,
    isAuthLoading: Boolean,
    isSyncing: Boolean,
    onSignIn: () -> Unit,
    onSignOut: () -> Unit,
    onSyncNow: () -> Unit,
) {
    when (authUiState) {
        is AuthUiState.SignedIn -> {
            Column {
                Text(
                    text = authUiState.user.displayName ?: authUiState.user.email ?: "",
                    style = AppTypography.bodyLarge.copy(color = MaterialTheme.colorScheme.onBackground),
                )
                if (authUiState.user.email != null && authUiState.user.displayName != null) {
                    Text(
                        text = authUiState.user.email ?: "",
                        style = AppTypography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurface),
                    )
                }
                Spacer(modifier = Modifier.height(DP12))
                Row {
                    OutlinedButton(
                        onClick = onSyncNow,
                        enabled = !isAuthLoading && !isSyncing,
                    ) {
                        if (isSyncing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(DP16),
                                strokeWidth = DP2,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        } else {
                            Text(text = stringResource(R.string.settings_sync_now))
                        }
                    }
                    Spacer(modifier = Modifier.width(DP8))
                    OutlinedButton(
                        onClick = onSignOut,
                        enabled = !isAuthLoading && !isSyncing,
                        colors =
                            ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error,
                            ),
                    ) {
                        Text(text = stringResource(R.string.settings_sign_out))
                    }
                }
            }
        }

        else -> {
            Button(
                onClick = onSignIn,
                enabled = !isAuthLoading,
            ) {
                if (isAuthLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(DP16),
                        strokeWidth = DP2,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                } else {
                    Text(text = stringResource(R.string.settings_sign_in_with_google))
                }
            }
        }
    }
}

@Composable
private fun LanguageOptionItem(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(vertical = DP12),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors =
                RadioButtonDefaults.colors(
                    selectedColor = MaterialTheme.colorScheme.primary,
                ),
        )

        Spacer(modifier = Modifier.width(DP12))

        Text(
            text = label,
            style = AppTypography.bodyLarge.copy(color = MaterialTheme.colorScheme.onBackground),
        )
    }
}

@Composable
private fun ThemeOptionItem(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    previewDark: Boolean?,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(vertical = DP12),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors =
                RadioButtonDefaults.colors(
                    selectedColor = MaterialTheme.colorScheme.primary,
                ),
        )

        Spacer(modifier = Modifier.width(DP12))

        Text(
            text = label,
            style = AppTypography.bodyLarge.copy(color = MaterialTheme.colorScheme.onBackground),
            modifier = Modifier.weight(1f),
        )

        if (previewDark != null) {
            ThemePreviewCard(dark = previewDark)
        } else {
            ThemePreviewCardSplit()
        }
    }
}

@Composable
private fun ThemePreviewCard(dark: Boolean) {
    val bgColor = if (dark) DarkBackground else LightBackground
    val surfaceColor = if (dark) DarkSurface else LightSurface
    val accentColor = if (dark) DarkPrimary else LightPrimary

    Box(
        modifier =
            Modifier
                .size(width = DP24 * 2, height = DP24)
                .clip(RoundedCornerShape(DP4))
                .border(
                    width = DP2,
                    color = MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(DP4),
                )
                .background(bgColor),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth(0.6f)
                    .height(DP8)
                    .align(Alignment.TopStart)
                    .padding(start = DP4, top = DP4)
                    .clip(RoundedCornerShape(DP2))
                    .background(surfaceColor),
        )
        Box(
            modifier =
                Modifier
                    .size(width = DP24, height = DP8)
                    .align(Alignment.BottomEnd)
                    .padding(end = DP4, bottom = DP4)
                    .clip(RoundedCornerShape(DP2))
                    .background(accentColor),
        )
    }
}

@Composable
private fun ThemePreviewCardSplit() {
    val shape = RoundedCornerShape(DP4)
    Row(
        modifier =
            Modifier
                .size(width = DP24 * 2, height = DP24)
                .clip(shape)
                .border(
                    width = DP2,
                    color = MaterialTheme.colorScheme.outline,
                    shape = shape,
                ),
    ) {
        Box(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .background(LightBackground),
        ) {
            Box(
                modifier =
                    Modifier
                        .size(DP8)
                        .align(Alignment.BottomEnd)
                        .padding(end = DP2, bottom = DP2)
                        .clip(RoundedCornerShape(DP2))
                        .background(LightPrimary),
            )
        }
        Box(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .background(DarkBackground),
        ) {
            Box(
                modifier =
                    Modifier
                        .size(DP8)
                        .align(Alignment.BottomEnd)
                        .padding(end = DP2, bottom = DP2)
                        .clip(RoundedCornerShape(DP2))
                        .background(DarkPrimary),
            )
        }
    }
}

@Preview
@Composable
private fun SettingsScreenPreview() {
    SettingsScreen(
        themeConfig = ThemeConfig.FOLLOW_SYSTEM,
        onThemeSelect = {},
        languageConfig = LanguageConfig.FOLLOW_SYSTEM,
        onLanguageSelect = {},
        authUiState = AuthUiState.Idle,
        isAuthLoading = false,
        isSyncing = false,
        onSignIn = {},
        onSignOut = {},
        onSignOutConfirmed = {},
        onSignOutDismissed = {},
        showSignOutConfirm = false,
        onSyncNow = {},
        snackbarHostState = SnackbarHostState(),
        onBack = {},
    )
}
