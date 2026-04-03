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
    val Moods =
        listOf(
            Mood("\uD83D\uDCDD 메모", MoodType.MEMO),
            Mood("\uD83D\uDE0A 기쁨", MoodType.POSITIVE),
            Mood("\uD83E\uDD70 행복", MoodType.POSITIVE),
            Mood("\uD83E\uDD29 설렘", MoodType.POSITIVE),
            Mood("\uD83D\uDE0D 사랑", MoodType.POSITIVE),
            Mood("\uD83D\uDE0E 뿌듯함", MoodType.POSITIVE),
            Mood("\uD83D\uDE10 무난함", MoodType.NEUTRAL),
            Mood("\uD83E\uDD14 고민", MoodType.NEUTRAL),
            Mood("\uD83D\uDE34 피곤", MoodType.NEUTRAL),
            Mood("\uD83D\uDE22 슬픔", MoodType.NEGATIVE),
            Mood("\uD83D\uDE21 화남", MoodType.NEGATIVE),
            Mood("\uD83D\uDE30 불안함", MoodType.NEGATIVE),
            Mood("\uD83D\uDE1E 실망함", MoodType.NEGATIVE),
            Mood("\uD83D\uDE29 피곤함", MoodType.NEGATIVE),
        )
}
