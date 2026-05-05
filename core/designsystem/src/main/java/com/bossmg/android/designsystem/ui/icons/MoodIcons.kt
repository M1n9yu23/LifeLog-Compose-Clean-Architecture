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
package com.bossmg.android.designsystem.ui.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bedtime
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Help
import androidx.compose.material.icons.rounded.Hotel
import androidx.compose.material.icons.rounded.SentimentDissatisfied
import androidx.compose.material.icons.rounded.SentimentNeutral
import androidx.compose.material.icons.rounded.SentimentVeryDissatisfied
import androidx.compose.material.icons.rounded.SentimentVerySatisfied
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.ThumbDown
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.ui.graphics.vector.ImageVector

object MoodIcons {
    fun forLabel(label: String): ImageVector =
        when (label) {
            "메모" -> Icons.Rounded.Edit
            "기쁨" -> Icons.Rounded.SentimentVerySatisfied
            "행복" -> Icons.Rounded.Favorite
            "설렘" -> Icons.Rounded.Star
            "사랑" -> Icons.Rounded.FavoriteBorder
            "뿌듯함" -> Icons.Rounded.EmojiEvents
            "무난함" -> Icons.Rounded.SentimentNeutral
            "고민" -> Icons.Rounded.Help
            "피곤" -> Icons.Rounded.Hotel
            "슬픔" -> Icons.Rounded.SentimentDissatisfied
            "화남" -> Icons.Rounded.SentimentVeryDissatisfied
            "불안함" -> Icons.Rounded.Warning
            "실망함" -> Icons.Rounded.ThumbDown
            "피곤함" -> Icons.Rounded.Bedtime
            else -> Icons.Rounded.Edit
        }
}
