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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.bossmg.android.designsystem.ui.theme.DP10
import com.bossmg.android.designsystem.ui.theme.LightSurface
import com.bossmg.android.domain.util.MoodProvider
import java.time.LocalDate

@Composable
fun LifeLogCard(
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

@Preview
@Composable
private fun LifeLogCardPreview() {
    LifeLogCard {
        MemoCardItem(
            date = LocalDate.of(2025, 10, 3),
            title = "저녁 산책",
            mood = MoodProvider.Keys.JOY,
            img = "https://picsum.photos/id/237/200/300",
        )
    }
}
