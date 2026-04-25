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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import kotlinx.coroutines.launch

@Composable
internal fun Settings(
    onBack: () -> Unit,
    onRestartRequired: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val themeConfig by viewModel.themeConfig.collectAsStateWithLifecycle()
    val languageConfig by viewModel.languageConfig.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    SettingsScreen(
        themeConfig = themeConfig,
        onThemeSelect = viewModel::onThemeSelect,
        languageConfig = languageConfig,
        onLanguageSelect = { config ->
            if (config != languageConfig) {
                scope.launch {
                    viewModel.onLanguageSelect(config)
                }
            }
        },
        onBack = onBack,
    )
}

@Composable
private fun SettingsScreen(
    themeConfig: ThemeConfig,
    onThemeSelect: (ThemeConfig) -> Unit,
    languageConfig: LanguageConfig,
    onLanguageSelect: (LanguageConfig) -> Unit,
    onBack: () -> Unit,
) {
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
        onBack = {},
    )
}
