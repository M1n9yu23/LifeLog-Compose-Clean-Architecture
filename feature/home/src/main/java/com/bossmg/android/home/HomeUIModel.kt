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
package com.bossmg.android.home

import androidx.compose.ui.graphics.Color
import com.bossmg.android.designsystem.ui.theme.White
import java.time.LocalDate

internal data class HomeUIModel(
    val id: Int,
    val date: LocalDate,
    val title: String,
    val mood: String,
    val cardColor: Color = White,
    val img: String? = null,
)
