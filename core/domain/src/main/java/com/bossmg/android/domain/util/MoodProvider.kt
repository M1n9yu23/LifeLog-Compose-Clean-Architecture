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
package com.bossmg.android.domain.util

import com.bossmg.android.domain.enums.MoodType
import com.bossmg.android.domain.model.Mood

object MoodProvider {
    object Keys {
        const val MEMO = "메모"
        const val JOY = "기쁨"
        const val HAPPY = "행복"
        const val EXCITED = "설렘"
        const val LOVE = "사랑"
        const val PROUD = "뿌듯함"
        const val OKAY = "무난함"
        const val WORRIED = "고민"
        const val TIRED = "피곤"
        const val SAD = "슬픔"
        const val ANGRY = "화남"
        const val ANXIOUS = "불안함"
        const val DISAPPOINTED = "실망함"
        const val EXHAUSTED = "피곤함"
    }

    val Moods =
        listOf(
            Mood(Keys.MEMO, MoodType.MEMO),
            Mood(Keys.JOY, MoodType.POSITIVE),
            Mood(Keys.HAPPY, MoodType.POSITIVE),
            Mood(Keys.EXCITED, MoodType.POSITIVE),
            Mood(Keys.LOVE, MoodType.POSITIVE),
            Mood(Keys.PROUD, MoodType.POSITIVE),
            Mood(Keys.OKAY, MoodType.NEUTRAL),
            Mood(Keys.WORRIED, MoodType.NEUTRAL),
            Mood(Keys.TIRED, MoodType.NEUTRAL),
            Mood(Keys.SAD, MoodType.NEGATIVE),
            Mood(Keys.ANGRY, MoodType.NEGATIVE),
            Mood(Keys.ANXIOUS, MoodType.NEGATIVE),
            Mood(Keys.DISAPPOINTED, MoodType.NEGATIVE),
            Mood(Keys.EXHAUSTED, MoodType.NEGATIVE),
        )
}
