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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.bossmg.android.designsystem.ui.components.CustomCard
import com.bossmg.android.designsystem.ui.components.MemoCardItem
import com.bossmg.android.designsystem.ui.theme.AppTypography
import com.bossmg.android.designsystem.ui.theme.Background
import com.bossmg.android.designsystem.ui.theme.Black
import com.bossmg.android.designsystem.ui.theme.DP12
import com.bossmg.android.designsystem.ui.theme.DP16
import com.bossmg.android.designsystem.ui.theme.DP36
import com.bossmg.android.designsystem.ui.theme.DP8
import com.bossmg.android.designsystem.ui.theme.White
import java.time.LocalDate
import kotlin.math.ceil

@Composable
fun Calendar() {
    var currentMonth by remember { mutableStateOf(LocalDate.now()) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    val uiMoel = CalendarUIModel(
        listOf(
            MemoItem(
                id = 1,
                date = LocalDate.of(2025, 10, 1),
                title = "오늘의 아침",
                mood = "행복"
            ),
            MemoItem(
                id = 2,
                date = LocalDate.of(2025, 10, 2),
                title = "점심시간",
                mood = "피곤"
            ),
            MemoItem(
                id = 3,
                date = LocalDate.of(2025, 10, 3),
                title = "저녁 산책",
                mood = "편안",
                img = "https://picsum.photos/id/237/200/300"
            ),
            MemoItem(
                id = 4,
                date = LocalDate.of(2025, 10, 4),
                title = "영화 감상",
                mood = "즐거움"
            ),
            MemoItem(
                id = 5,
                date = LocalDate.of(2025, 10, 5),
                title = "카페에서 작업",
                mood = "집중"
            )
        )
    )

    CalendarScreen(
        uiModel = uiMoel,
        currentMonth = currentMonth,
        selectedDate = selectedDate,
        onDateSelected = {
            selectedDate = it
        },
        onPrevMonth = {
            currentMonth = currentMonth.minusMonths(1)
        },
        onNextMonth = {
            currentMonth = currentMonth.plusMonths(1)
        }
    )
}

@Composable
private fun CalendarScreen(
    uiModel: CalendarUIModel,
    currentMonth: LocalDate,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .statusBarsPadding()
            .padding(DP12)
    ) {
        item {
            CalenderHeader(
                month = currentMonth,
                onPrevMonth = onPrevMonth,
                onNextMonth = onNextMonth
            )

            Spacer(Modifier.height(DP12))

            CalendarGrid(
                month = currentMonth,
                selectedDate = selectedDate,
                onDateSelected = onDateSelected
            )

            Spacer(Modifier.height(DP16))
        }

        items(uiModel.memoItems, key = {
            it.id
        }) {
            MemoItemCard(it)
        }
    }
}

@Composable
private fun CalenderHeader(
    month: LocalDate,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = {
                onPrevMonth()
            }
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "이전 달"
            )
        }

        Text(
            text = "${month.monthValue}월 ${month.year}",
            style = AppTypography.titleMedium
        )

        IconButton({
            onNextMonth()
        }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "다음 달"
            )
        }
    }
}

@Composable
private fun CalendarGrid(
    month: LocalDate,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    val daysOfWeek = listOf(
        stringResource(R.string.text_week_sunday),
        stringResource(R.string.text_week_monday),
        stringResource(R.string.text_week_tuesday),
        stringResource(R.string.text_week_wednesday),
        stringResource(R.string.text_week_thursday),
        stringResource(R.string.text_week_friday),
        stringResource(R.string.text_week_saturday)
    )

    val firstDay = month.withDayOfMonth(1)
    val daysInMonth = month.lengthOfMonth()
    val startOffset = firstDay.dayOfWeek.value % 7

    Column {
        Row(modifier = Modifier.fillMaxWidth()) {
            daysOfWeek.forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = AppTypography.bodyMedium
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
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .clickable {
                                    onDateSelected(date)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(DP36)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isSelected -> Color.Cyan
                                            isToday -> Color.LightGray
                                            else -> Color.Transparent
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = day.toString(),
                                    color = if (isSelected) White else Black
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
private fun MemoItemCard(item: MemoItem) {
    CustomCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = DP8),
        backgroundColor = item.cardColor
    ) {
        MemoCardItem(
            date = item.date,
            title = item.title,
            mood = item.mood,
            img = item.img
        )
    }
}

@Preview
@Composable
private fun CalendarScreenPreview() {
    CalendarScreen(
        uiModel = CalendarUIModel(
            listOf(
                MemoItem(
                    id = 1,
                    date = LocalDate.of(2025, 10, 1),
                    title = "오늘의 아침",
                    mood = "행복"
                ),
                MemoItem(
                    id = 2,
                    date = LocalDate.of(2025, 10, 2),
                    title = "점심시간",
                    mood = "피곤"
                ),
            )
        ),
        currentMonth = LocalDate.of(2025, 10, 1),
        selectedDate = LocalDate.of(2025, 10, 10),
        onDateSelected = {},
        onPrevMonth = {},
        onNextMonth = {}
    )
}