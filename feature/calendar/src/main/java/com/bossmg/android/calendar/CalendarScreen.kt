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
package com.bossmg.android.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bossmg.android.designsystem.ui.components.CustomCard
import com.bossmg.android.designsystem.ui.components.LoadingScreen
import com.bossmg.android.designsystem.ui.components.MemoCardItem
import com.bossmg.android.designsystem.ui.icons.LifeIcons
import com.bossmg.android.designsystem.ui.theme.AppTypography
import com.bossmg.android.designsystem.ui.theme.DP12
import com.bossmg.android.designsystem.ui.theme.DP16
import com.bossmg.android.designsystem.ui.theme.DP36
import com.bossmg.android.designsystem.ui.theme.DP4
import com.bossmg.android.designsystem.ui.theme.DP8
import com.bossmg.android.designsystem.ui.theme.LocalLifeLogColors
import com.bossmg.android.designsystem.ui.util.cardColor
import com.bossmg.android.model.MemoItem
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import java.time.LocalDate
import kotlin.math.ceil

@Composable
internal fun Calendar(
    onMemoItemClick: (Int) -> Unit,
    viewModel: CalendarViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (uiState) {
        is CalendarUiState.Loading -> {
            LoadingScreen()
        }

        is CalendarUiState.Success -> {
            CalendarScreen(
                state = uiState as CalendarUiState.Success,
                onDateSelect = {
                    viewModel.onDateSelect(it)
                },
                onPrevMonth = {
                    viewModel.onClickPrevMonth()
                },
                onNextMonth = {
                    viewModel.onClickNextMonth()
                },
                onMemoItemClick = onMemoItemClick,
            )
        }

        is CalendarUiState.Error -> {
        }
    }
}

@Composable
private fun CalendarScreen(
    state: CalendarUiState.Success,
    onDateSelect: (LocalDate) -> Unit,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onMemoItemClick: (Int) -> Unit,
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
            CalenderHeader(
                month = state.currentMonth,
                onPrevMonth = onPrevMonth,
                onNextMonth = onNextMonth,
            )

            Spacer(Modifier.height(DP12))

            CalendarGrid(
                month = state.currentMonth,
                selectedDate = state.selectedDate,
                markedDate = state.markedDates,
                onDateSelect = onDateSelect,
            )

            Spacer(Modifier.height(DP16))
        }

        items(state.memoItems, key = {
            it.id
        }) {
            MemoItemCard(it, onMemoItemClick)
        }
    }
}

@Composable
private fun CalenderHeader(
    month: LocalDate,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            onClick = {
                onPrevMonth()
            },
        ) {
            Icon(
                imageVector = LifeIcons.ArrowLeft,
                contentDescription = stringResource(R.string.text_prev_month),
            )
        }

        Text(
            text = "${month.monthValue}월 ${month.year}",
            style = AppTypography.titleMedium,
        )

        IconButton({
            onNextMonth()
        }) {
            Icon(
                imageVector = LifeIcons.ArrowRight,
                contentDescription = stringResource(R.string.text_next_month),
            )
        }
    }
}

@Composable
private fun CalendarGrid(
    month: LocalDate,
    selectedDate: LocalDate,
    markedDate: ImmutableList<LocalDate>,
    onDateSelect: (LocalDate) -> Unit,
) {
    val daysOfWeek =
        listOf(
            stringResource(R.string.text_week_sunday),
            stringResource(R.string.text_week_monday),
            stringResource(R.string.text_week_tuesday),
            stringResource(R.string.text_week_wednesday),
            stringResource(R.string.text_week_thursday),
            stringResource(R.string.text_week_friday),
            stringResource(R.string.text_week_saturday),
        )

    val firstDay = month.withDayOfMonth(1)
    val daysInMonth = month.lengthOfMonth()
    val startOffset = firstDay.dayOfWeek.value % 7
    val calendarMarker = LocalLifeLogColors.current.calendarMarker

    Column {
        Row(modifier = Modifier.fillMaxWidth()) {
            daysOfWeek.forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = AppTypography.bodyMedium,
                )
            }
        }

        Spacer(Modifier.height(DP8))

        val rows = ceil((startOffset + daysInMonth) / 7.0).toInt()
        repeat(rows) { row ->
            Row(modifier = Modifier.fillMaxWidth()) {
                for (col in 0 until 7) {
                    val index = row * 7 + col
                    val day = index - startOffset + 1
                    if (index < startOffset || day > daysInMonth) {
                        Spacer(modifier = Modifier.weight(1f))
                    } else {
                        val date = month.withDayOfMonth(day)
                        val isSelected = date == selectedDate
                        val isToday = date == LocalDate.now()

                        Box(
                            modifier =
                                Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .clickable {
                                        onDateSelect(date)
                                    },
                            contentAlignment = Alignment.Center,
                        ) {
                            Box(
                                modifier =
                                    Modifier
                                        .size(DP36)
                                        .clip(CircleShape)
                                        .background(
                                            when {
                                                isSelected -> MaterialTheme.colorScheme.primary
                                                isToday -> MaterialTheme.colorScheme.secondaryContainer
                                                else -> Color.Transparent
                                            },
                                        ),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = day.toString(),
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground,
                                )
                            }

                            if (markedDate.contains(date)) {
                                Box(
                                    modifier =
                                        Modifier
                                            .size(DP4)
                                            .background(calendarMarker, CircleShape)
                                            .align(Alignment.BottomCenter),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MemoItemCard(item: MemoItem, onMemoItemClick: (Int) -> Unit) {
    CustomCard(
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

@Preview
@Composable
private fun CalendarScreenPreview() {
    val sampleDate = LocalDate.of(2025, 10, 10)
    CalendarScreen(
        state =
            CalendarUiState.Success(
                currentMonth = LocalDate.of(2025, 10, 1),
                selectedDate = sampleDate,
                markedDates = persistentListOf(sampleDate),
                memoItems =
                    persistentListOf(
                        MemoItem(
                            id = 1,
                            date = sampleDate,
                            title = "오늘의 아침",
                            mood = "행복",
                        ),
                        MemoItem(
                            id = 2,
                            date = LocalDate.of(2025, 10, 2),
                            title = "점심시간",
                            mood = "피곤",
                        ),
                    ),
            ),
        onDateSelect = {},
        onPrevMonth = {},
        onNextMonth = {},
        onMemoItemClick = {},
    )
}
