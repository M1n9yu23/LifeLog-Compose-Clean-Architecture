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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import coil.compose.AsyncImage
import com.bossmg.android.designsystem.ui.theme.AppTypography
import com.bossmg.android.designsystem.ui.theme.DP10
import com.bossmg.android.designsystem.ui.theme.DP16
import com.bossmg.android.designsystem.ui.theme.DP4
import com.bossmg.android.designsystem.ui.theme.DP80
import com.bossmg.android.designsystem.ui.theme.LightSurface
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CustomCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = LightSurface,
    shadowColor: Color = Color.Transparent,
    shapeTop: Dp = DP10,
    shapeBottom: Dp = DP10,
    elevation: Dp = DP10,
    content: @Composable ColumnScope.() -> Unit,
) {
    val shape =
        RoundedCornerShape(
            topStart = shapeTop,
            topEnd = shapeTop,
            bottomStart = shapeBottom,
            bottomEnd = shapeBottom,
        )

    Box(
        modifier =
            modifier
                .shadow(
                    elevation = elevation,
                    shape = shape,
                    ambientColor = shadowColor,
                    spotColor = shadowColor,
                )
                .background(
                    backgroundColor,
                    shape = shape,
                ),
    ) {
        Column(content = content)
    }
}

@Composable
fun MemoCardItem(
    date: LocalDate,
    title: String,
    mood: String,
    img: String?,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(DP16),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = date.dayOfMonth.toString(),
                style = AppTypography.titleLarge.copy(color = MaterialTheme.colorScheme.onSurface),
            )
            Text(
                text =
                    date.dayOfWeek.getDisplayName(
                        TextStyle.FULL,
                        Locale.getDefault(),
                    ),
                style = AppTypography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
            )
        }

        Spacer(Modifier.width(DP16))

        Column(
            modifier =
                Modifier
                    .weight(1f),
        ) {
            Text(
                text =
                    date.format(
                        DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)
                            .withLocale(Locale.getDefault()),
                    ),
                style =
                    AppTypography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontStyle = FontStyle.Italic,
                    ),
            )

            Spacer(modifier = Modifier.height(DP4))

            Text(
                text = title,
                style = AppTypography.titleLarge.copy(color = MaterialTheme.colorScheme.onSurface),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(DP4))

            Text(
                text = mood,
                style = AppTypography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
            )
        }

        img?.let {
            AsyncImage(
                model = it,
                contentDescription = null,
                modifier =
                    Modifier
                        .size(DP80)
                        .clip(RoundedCornerShape(DP10)),
                contentScale = ContentScale.Crop,
            )
        }
    }
}

@Preview
@Composable
private fun CustomCardPreview() {
    CustomCard {
        MemoCardItem(
            date = LocalDate.of(2025, 10, 3),
            title = "저녁 산책",
            mood = "편안",
            img = "https://picsum.photos/id/237/200/300",
        )
    }
}
