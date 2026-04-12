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
package com.bossmg.android.designsystem.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

private val LightColorScheme =
    lightColorScheme(
        primary = LightPrimary,
        onPrimary = LightOnPrimary,
        primaryContainer = LightPrimaryContainer,
        onPrimaryContainer = LightOnPrimaryContainer,
        background = LightBackground,
        onBackground = LightOnBackground,
        surface = LightSurface,
        surfaceVariant = LightSurfaceVariant,
        onSurface = LightOnSurface,
        onSurfaceVariant = LightOnSurfaceVariant,
        outline = LightOutline,
        secondaryContainer = LightSecondaryContainer,
    )

private val DarkColorScheme =
    darkColorScheme(
        primary = DarkPrimary,
        onPrimary = DarkOnPrimary,
        primaryContainer = DarkPrimaryContainer,
        onPrimaryContainer = DarkOnPrimaryContainer,
        background = DarkBackground,
        onBackground = DarkOnBackground,
        surface = DarkSurface,
        surfaceVariant = DarkSurfaceVariant,
        onSurface = DarkOnSurface,
        onSurfaceVariant = DarkOnSurfaceVariant,
        outline = DarkOutline,
        secondaryContainer = DarkSecondaryContainer,
    )

@Composable
fun LifeLogTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val lifeLogColors = if (darkTheme) darkLifeLogColors() else lightLifeLogColors()

    CompositionLocalProvider(LocalLifeLogColors provides lifeLogColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            content = content,
        )
    }
}
