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
package com.bossmg.android.mood

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bossmg.android.designsystem.ui.components.LifeLogCard
import com.bossmg.android.designsystem.ui.components.LoadingScreen
import com.bossmg.android.designsystem.ui.components.MemoCardItem
import com.bossmg.android.designsystem.ui.icons.MoodIcons
import com.bossmg.android.designsystem.ui.theme.AppTypography
import com.bossmg.android.designsystem.ui.theme.DP1
import com.bossmg.android.designsystem.ui.theme.DP10
import com.bossmg.android.designsystem.ui.theme.DP12
import com.bossmg.android.designsystem.ui.theme.DP16
import com.bossmg.android.designsystem.ui.theme.DP24
import com.bossmg.android.designsystem.ui.theme.DP8
import com.bossmg.android.designsystem.ui.theme.DP800
import com.bossmg.android.designsystem.ui.theme.LocalLifeLogColors
import com.bossmg.android.designsystem.ui.util.cardColor
import com.bossmg.android.designsystem.ui.util.moodLabel
import com.bossmg.android.domain.enums.MoodType
import com.bossmg.android.domain.util.MoodProvider
import com.bossmg.android.model.MemoItem
import java.time.LocalDate

@Composable
internal fun Mood(
    onMemoItemClick: (String) -> Unit,
    viewModel: MoodViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is MoodUIState.Loading -> {
            LoadingScreen()
        }

        is MoodUIState.Success -> {
            MoodScreen(
                uiModel = state.uiModel,
                selectedMood = state.selectedMood,
                onMoodSelected = {
                    viewModel.selectMood(it)
                },
                onMemoItemClick = onMemoItemClick,
            )
        }
    }
}

@Composable
private fun MoodScreen(
    uiModel: MoodUIModel,
    selectedMood: String,
    onMoodSelected: (String) -> Unit,
    onMemoItemClick: (String) -> Unit,
) {
    LazyColumn(
        modifier =
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .statusBarsPadding()
                .padding(DP12),
    ) {
        item {
            Column(
                modifier = Modifier.fillMaxSize(),
            ) {
                Text(
                    text = stringResource(R.string.text_mood),
                    style = AppTypography.titleLarge.copy(color = MaterialTheme.colorScheme.onSurface),
                )

                if (uiModel.moods.isNotEmpty()) {
                    Spacer(Modifier.height(DP16))

                    MoodsBox(
                        moods = uiModel.moods,
                        selectedMood = selectedMood,
                        onMoodSelected = onMoodSelected,
                    )

                    Spacer(Modifier.height(DP24))
                }
            }
        }

        items(uiModel.memoItem) {
            MemoItemCard(it, onMemoItemClick)
        }
    }
}

@Composable
private fun MemoItemCard(item: MemoItem, onMemoItemClick: (String) -> Unit) {
    LifeLogCard(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable {
                    onMemoItemClick(item.id)
                }
                .padding(vertical = DP8),
        backgroundColor = cardColor(item.mood),
    ) {
        MemoCardItem(
            date = item.date,
            title = item.title,
            mood = item.mood,
            img = item.img,
        )
    }
}

@Composable
private fun MoodsBox(
    moods: Map<String, Int>,
    selectedMood: String,
    onMoodSelected: (String) -> Unit,
) {
    LifeLogCard(
        modifier =
            Modifier
                .fillMaxWidth()
                .border(width = DP1, color = MaterialTheme.colorScheme.outline, shape = RoundedCornerShape(DP10)),
        shapeTop = DP10,
        shapeBottom = DP10,
        elevation = DP10,
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier =
                Modifier
                    .fillMaxWidth()
                    .heightIn(max = DP800)
                    .padding(DP12),
            horizontalArrangement = Arrangement.spacedBy(DP8),
            verticalArrangement = Arrangement.spacedBy(DP8),
            userScrollEnabled = false,
        ) {
            items(moods.entries.toList(), key = { it.key }) { (label, count) ->
                MoodStatCard(
                    label = label,
                    count = count,
                    isSelected = label == selectedMood,
                    onClick = { onMoodSelected(label) },
                )
            }
        }
    }
}

@Composable
private fun MoodStatCard(
    label: String,
    count: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val lifeLogColors = LocalLifeLogColors.current
    val moodType =
        MoodProvider.Moods.firstOrNull { it.key == label }?.type ?: MoodType.MEMO

    val bgColor =
        when (moodType) {
            MoodType.POSITIVE -> lifeLogColors.moodPositiveBg
            MoodType.NEUTRAL -> lifeLogColors.moodNeutralBg
            MoodType.NEGATIVE -> lifeLogColors.moodNegativeBg
            MoodType.MEMO -> lifeLogColors.moodMemoBg
        }
    val strokeColor =
        when (moodType) {
            MoodType.POSITIVE -> lifeLogColors.moodPositiveStroke
            MoodType.NEUTRAL -> lifeLogColors.moodNeutralStroke
            MoodType.NEGATIVE -> lifeLogColors.moodNegativeStroke
            MoodType.MEMO -> lifeLogColors.moodMemoStroke
        }

    val animatedBorderColor by animateColorAsState(
        targetValue = if (isSelected) strokeColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
        label = "moodCardBorder",
    )
    val animatedBg by animateColorAsState(
        targetValue = if (isSelected) bgColor else MaterialTheme.colorScheme.surfaceVariant,
        label = "moodCardBg",
    )

    Row(
        modifier =
            Modifier
                .clip(RoundedCornerShape(DP10))
                .border(DP1, animatedBorderColor, RoundedCornerShape(DP10))
                .background(animatedBg)
                .clickable(onClick = onClick)
                .padding(horizontal = DP16, vertical = DP12),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(DP10),
    ) {
        val displayLabel = moodLabel(label)
        Icon(
            imageVector = MoodIcons.forLabel(label),
            contentDescription = displayLabel,
            modifier = Modifier.size(DP24),
            tint = if (isSelected) strokeColor else MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = displayLabel,
                style =
                    AppTypography.bodyMedium.copy(
                        color = if (isSelected) strokeColor else MaterialTheme.colorScheme.onSurface,
                    ),
            )
            Text(
                text = count.toString(),
                style =
                    AppTypography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
            )
        }
    }
}

@Preview
@Composable
private fun MoodScreenPreview() {
    MoodScreen(
        MoodUIModel(
            moods =
                mapOf(
                    MoodProvider.Keys.MEMO to 1,
                    MoodProvider.Keys.JOY to 2,
                    MoodProvider.Keys.SAD to 3,
                    MoodProvider.Keys.EXCITED to 1,
                    MoodProvider.Keys.ANXIOUS to 2,
                ),
            memoItem =
                listOf(
                    MemoItem(
                        id = "1",
                        date = LocalDate.of(2025, 10, 1),
                        title = "오늘의 아침",
                        mood = MoodProvider.Keys.JOY,
                    ),
                    MemoItem(
                        id = "2",
                        date = LocalDate.of(2025, 10, 2),
                        title = "점심시간",
                        mood = MoodProvider.Keys.TIRED,
                    ),
                    MemoItem(
                        id = "3",
                        date = LocalDate.of(2025, 10, 3),
                        title = "저녁 산책",
                        mood = MoodProvider.Keys.JOY,
                        img = "https://picsum.photos/id/237/200/300",
                    ),
                ),
        ),
        selectedMood = MoodProvider.Keys.JOY,
        onMoodSelected = {},
        onMemoItemClick = {},
    )
}
