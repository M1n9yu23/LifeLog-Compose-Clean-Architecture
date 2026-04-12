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

import androidx.compose.runtime.Stable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Stable
data class LifeLogColors(
    val moodPositiveBg: Color,
    val moodPositiveStroke: Color,
    val moodNeutralBg: Color,
    val moodNeutralStroke: Color,
    val moodNegativeBg: Color,
    val moodNegativeStroke: Color,
    val moodMemoBg: Color,
    val moodMemoStroke: Color,
    val calendarMarker: Color,
)

fun lightLifeLogColors() =
    LifeLogColors(
        moodPositiveBg = LightMoodPositiveBg,
        moodPositiveStroke = LightMoodPositiveStroke,
        moodNeutralBg = LightMoodNeutralBg,
        moodNeutralStroke = LightMoodNeutralStroke,
        moodNegativeBg = LightMoodNegativeBg,
        moodNegativeStroke = LightMoodNegativeStroke,
        moodMemoBg = LightMoodMemoBg,
        moodMemoStroke = LightMoodMemoStroke,
        calendarMarker = LightCalendarMarker,
    )

fun darkLifeLogColors() =
    LifeLogColors(
        moodPositiveBg = DarkMoodPositiveBg,
        moodPositiveStroke = DarkMoodPositiveStroke,
        moodNeutralBg = DarkMoodNeutralBg,
        moodNeutralStroke = DarkMoodNeutralStroke,
        moodNegativeBg = DarkMoodNegativeBg,
        moodNegativeStroke = DarkMoodNegativeStroke,
        moodMemoBg = DarkMoodMemoBg,
        moodMemoStroke = DarkMoodMemoStroke,
        calendarMarker = DarkCalendarMarker,
    )

val LocalLifeLogColors = staticCompositionLocalOf { lightLifeLogColors() }
