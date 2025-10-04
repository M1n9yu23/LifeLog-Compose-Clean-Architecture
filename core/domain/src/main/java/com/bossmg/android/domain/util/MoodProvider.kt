package com.bossmg.android.domain.util

import com.bossmg.android.domain.enums.MoodType
import com.bossmg.android.domain.model.Mood

object MoodProvider {
    val moods = listOf(
        Mood("\uD83D\uDCDD 메모", MoodType.MEMO),

        Mood("\uD83D\uDE0A 기쁨", MoodType.POSITIVE),
        Mood("\uD83E\uDD70 행복", MoodType.POSITIVE),
        Mood("\uD83E\uDD29 설렘", MoodType.POSITIVE),
        Mood("\uD83D\uDE0D 사랑", MoodType.POSITIVE),
        Mood("\uD83D\uDE0E 뿌듯함", MoodType.POSITIVE),

        Mood("\uD83D\uDE10 무난함", MoodType.NEUTRAL),
        Mood("\uD83E\uDD14 생각에 잠김", MoodType.NEUTRAL),
        Mood("\uD83D\uDE34 피곤", MoodType.NEUTRAL),

        Mood("\uD83D\uDE22 슬픔", MoodType.NEGATIVE),
        Mood("\uD83D\uDE21 화남", MoodType.NEGATIVE),
        Mood("\uD83D\uDE30 불안함", MoodType.NEGATIVE),
        Mood("\uD83D\uDE1E 실망함", MoodType.NEGATIVE),
        Mood("\uD83D\uDE29 피곤함", MoodType.NEGATIVE)
    )
}