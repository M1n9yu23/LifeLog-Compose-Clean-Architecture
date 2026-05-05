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
package com.bossmg.android.memo

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import com.bossmg.android.designsystem.ui.icons.MoodIcons
import com.bossmg.android.designsystem.ui.theme.AppTypography
import com.bossmg.android.designsystem.ui.theme.DP1
import com.bossmg.android.designsystem.ui.theme.DP10
import com.bossmg.android.designsystem.ui.theme.DP12
import com.bossmg.android.designsystem.ui.theme.DP16
import com.bossmg.android.designsystem.ui.theme.DP24
import com.bossmg.android.designsystem.ui.theme.DP32
import com.bossmg.android.designsystem.ui.theme.DP8
import com.bossmg.android.designsystem.ui.theme.LocalLifeLogColors
import com.bossmg.android.domain.enums.MoodType
import com.bossmg.android.domain.util.MoodProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MoodPickerBottomSheet(
    selectedMood: String,
    onMoodSelected: (String) -> Unit,
    onDismiss: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Text(
            text = stringResource(R.string.mood_picker_title),
            style = AppTypography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = DP16),
        )
        Spacer(Modifier.height(DP12))
        MoodPickerGrid(
            selectedMood = selectedMood,
            onMoodSelected = { label ->
                onMoodSelected(label)
                onDismiss()
            },
        )
        Spacer(Modifier.height(DP32))
    }
}

@Composable
private fun MoodPickerGrid(
    selectedMood: String,
    onMoodSelected: (String) -> Unit,
) {
    val sections =
        listOf(
            MoodType.POSITIVE,
            MoodType.NEUTRAL,
            MoodType.NEGATIVE,
            MoodType.MEMO,
        )

    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        contentPadding = PaddingValues(horizontal = DP16, vertical = DP8),
        horizontalArrangement = Arrangement.spacedBy(DP8),
        verticalArrangement = Arrangement.spacedBy(DP8),
        userScrollEnabled = false,
    ) {
        sections.forEach { type ->
            val sectionMoods = MoodProvider.Moods.filter { it.type == type }
            if (sectionMoods.isEmpty()) return@forEach

            item(span = { GridItemSpan(4) }) {
                MoodSectionHeader(type = type)
            }

            items(sectionMoods, key = { it.str }) { mood ->
                MoodCell(
                    label = mood.str,
                    type = mood.type,
                    isSelected = mood.str == selectedMood,
                    onClick = { onMoodSelected(mood.str) },
                )
            }
        }
    }
}

@Composable
private fun MoodSectionHeader(type: MoodType) {
    val lifeLogColors = LocalLifeLogColors.current
    val label =
        when (type) {
            MoodType.POSITIVE -> stringResource(R.string.mood_section_positive)
            MoodType.NEUTRAL -> stringResource(R.string.mood_section_neutral)
            MoodType.NEGATIVE -> stringResource(R.string.mood_section_negative)
            MoodType.MEMO -> stringResource(R.string.mood_section_memo)
        }
    val color =
        when (type) {
            MoodType.POSITIVE -> lifeLogColors.moodPositiveStroke
            MoodType.NEUTRAL -> lifeLogColors.moodNeutralStroke
            MoodType.NEGATIVE -> lifeLogColors.moodNegativeStroke
            MoodType.MEMO -> lifeLogColors.moodMemoStroke
        }
    Text(
        text = label,
        style = AppTypography.labelSmall.copy(color = color),
        modifier = Modifier.padding(top = DP8, bottom = DP8),
    )
}

@Composable
private fun MoodCell(
    label: String,
    type: MoodType,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val lifeLogColors = LocalLifeLogColors.current
    val bgColor =
        when (type) {
            MoodType.POSITIVE -> lifeLogColors.moodPositiveBg
            MoodType.NEUTRAL -> lifeLogColors.moodNeutralBg
            MoodType.NEGATIVE -> lifeLogColors.moodNegativeBg
            MoodType.MEMO -> lifeLogColors.moodMemoBg
        }
    val strokeColor =
        when (type) {
            MoodType.POSITIVE -> lifeLogColors.moodPositiveStroke
            MoodType.NEUTRAL -> lifeLogColors.moodNeutralStroke
            MoodType.NEGATIVE -> lifeLogColors.moodNegativeStroke
            MoodType.MEMO -> lifeLogColors.moodMemoStroke
        }

    val animatedBorderColor by animateColorAsState(
        targetValue = if (isSelected) strokeColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
        label = "moodCellBorder",
    )
    val animatedBg by animateColorAsState(
        targetValue = if (isSelected) bgColor else MaterialTheme.colorScheme.surfaceVariant,
        label = "moodCellBg",
    )

    Column(
        modifier =
            Modifier
                .clip(RoundedCornerShape(DP10))
                .border(DP1, animatedBorderColor, RoundedCornerShape(DP10))
                .background(animatedBg)
                .clickable(onClick = onClick)
                .padding(vertical = DP12),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(DP8),
    ) {
        Icon(
            imageVector = MoodIcons.forLabel(label),
            contentDescription = label,
            modifier = Modifier.size(DP24),
            tint = if (isSelected) strokeColor else MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = label,
            style =
                AppTypography.labelSmall.copy(
                    color = if (isSelected) strokeColor else MaterialTheme.colorScheme.onSurfaceVariant,
                ),
        )
    }
}
