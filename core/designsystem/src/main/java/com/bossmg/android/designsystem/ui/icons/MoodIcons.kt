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
import com.bossmg.android.domain.util.MoodProvider

object MoodIcons {
    fun forLabel(key: String): ImageVector =
        when (key) {
            MoodProvider.Keys.MEMO -> Icons.Rounded.Edit
            MoodProvider.Keys.JOY -> Icons.Rounded.SentimentVerySatisfied
            MoodProvider.Keys.HAPPY -> Icons.Rounded.Favorite
            MoodProvider.Keys.EXCITED -> Icons.Rounded.Star
            MoodProvider.Keys.LOVE -> Icons.Rounded.FavoriteBorder
            MoodProvider.Keys.PROUD -> Icons.Rounded.EmojiEvents
            MoodProvider.Keys.OKAY -> Icons.Rounded.SentimentNeutral
            MoodProvider.Keys.WORRIED -> Icons.Rounded.Help
            MoodProvider.Keys.TIRED -> Icons.Rounded.Hotel
            MoodProvider.Keys.SAD -> Icons.Rounded.SentimentDissatisfied
            MoodProvider.Keys.ANGRY -> Icons.Rounded.SentimentVeryDissatisfied
            MoodProvider.Keys.ANXIOUS -> Icons.Rounded.Warning
            MoodProvider.Keys.DISAPPOINTED -> Icons.Rounded.ThumbDown
            MoodProvider.Keys.EXHAUSTED -> Icons.Rounded.Bedtime
            else -> Icons.Rounded.Edit
        }
}
