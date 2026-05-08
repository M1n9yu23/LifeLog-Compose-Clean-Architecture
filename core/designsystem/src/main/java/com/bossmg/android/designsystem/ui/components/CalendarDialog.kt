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
package com.bossmg.android.designsystem.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.bossmg.android.designsystem.R
import com.bossmg.android.designsystem.ui.icons.LifeIcons
import com.bossmg.android.designsystem.ui.theme.AppTypography
import com.bossmg.android.designsystem.ui.theme.DP10
import com.bossmg.android.designsystem.ui.theme.DP12
import com.bossmg.android.designsystem.ui.theme.DP16
import com.bossmg.android.designsystem.ui.theme.DP2
import com.bossmg.android.designsystem.ui.theme.DP320
import com.bossmg.android.designsystem.ui.theme.DP8
import com.bossmg.android.designsystem.ui.util.currentJavaLocale
import com.bossmg.android.designsystem.ui.util.rememberLongDateFormatter
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.format.TextStyle
import kotlin.math.ceil

@Composable
fun CalendarDialog(
    initialDate: LocalDate = LocalDate.now(),
    onConfirm: (LocalDate) -> Unit = {},
    onCancel: () -> Unit = {},
) {
    var displayedMonth by remember { mutableStateOf(initialDate.withDayOfMonth(1)) }
    var selectedDate by remember { mutableStateOf(initialDate) }

    Dialog(
        onDismissRequest = onCancel,
        properties =
            DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
            ),
    ) {
        LifeLogCard(
            shapeTop = DP10,
            shapeBottom = DP10,
            elevation = DP10,
            backgroundColor = MaterialTheme.colorScheme.surface,
        ) {
            Column(
                modifier =
                    Modifier
                        .width(DP320)
                        .padding(DP16)
                        .wrapContentSize(),
            ) {
                val longFormatter = rememberLongDateFormatter()
                Text(
                    text = selectedDate.format(longFormatter),
                    style = AppTypography.titleMedium.copy(MaterialTheme.colorScheme.onSurface),
                )

                Spacer(Modifier.height(DP10))

                LifeLogDivider()

                CalenderHeader(
                    month = displayedMonth,
                    onPrevMonth = { displayedMonth = displayedMonth.minusMonths(1) },
                    onNextMonth = { displayedMonth = displayedMonth.plusMonths(1) },
                )

                Spacer(Modifier.height(DP12))

                val currentLocale = currentJavaLocale
                val dayNames =
                    remember(currentLocale) {
                        (0..6).map { DayOfWeek.of(if (it == 0) 7 else it).getDisplayName(TextStyle.NARROW, currentLocale) }
                    }
                Row(modifier = Modifier.fillMaxWidth()) {
                    dayNames.forEach { day ->
                        Text(
                            text = day,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center,
                            style = AppTypography.bodyMedium,
                        )
                    }
                }

                Spacer(Modifier.height(DP8))

                CalendarGrid(
                    month = displayedMonth,
                    selectedDate = selectedDate,
                    onDateSelected = { selectedDate = it },
                )

                Spacer(Modifier.height(DP16))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(
                        onClick = onCancel,
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onBackground,
                            ),
                    ) {
                        Text(stringResource(R.string.dialog_cancel))
                    }
                    Spacer(Modifier.width(DP8))

                    TextButton(
                        onClick = { onConfirm(selectedDate) },
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary,
                            ),
                    ) {
                        Text(stringResource(R.string.dialog_confirm))
                    }
                }
            }
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
        IconButton(onClick = onPrevMonth) {
            Icon(LifeIcons.ArrowLeft, contentDescription = stringResource(R.string.cd_prev_month))
        }

        Text(
            text = month.format(DateTimeFormatter.ofPattern("MMMM yyyy").withLocale(currentJavaLocale)),
            style = AppTypography.titleMedium,
            textAlign = TextAlign.Center,
        )

        IconButton(onClick = onNextMonth) {
            Icon(LifeIcons.ArrowRight, contentDescription = stringResource(R.string.cd_next_month))
        }
    }
}

@Composable
private fun CalendarGrid(
    month: LocalDate,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
) {
    val firstDay = month.withDayOfMonth(1)
    val daysInMonth = month.lengthOfMonth()
    val startOffset = firstDay.dayOfWeek.value % 7

    val rows = ceil((startOffset + daysInMonth) / 7.0).toInt()

    Column {
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
                                    .padding(DP2)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isSelected -> MaterialTheme.colorScheme.primary
                                            isToday -> MaterialTheme.colorScheme.secondaryContainer
                                            else -> Color.Transparent
                                        },
                                    )
                                    .clickable { onDateSelected(date) },
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = day.toString(),
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun CalendarDialogPreview() {
    CalendarDialog()
}
