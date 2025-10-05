package com.bossmg.android.designsystem.ui.util

import androidx.compose.ui.graphics.Color
import com.bossmg.android.designsystem.ui.theme.MoodMemoBg
import com.bossmg.android.designsystem.ui.theme.MoodNegativeBg
import com.bossmg.android.designsystem.ui.theme.MoodNeutralBg
import com.bossmg.android.designsystem.ui.theme.MoodPositiveBg
import com.bossmg.android.domain.enums.MoodType
import com.bossmg.android.domain.util.MoodProvider

fun cardColor(mood: String): Color {
    val moodType = MoodProvider.Moods.firstOrNull { it.str == mood }?.type ?: MoodType.MEMO

    return when (moodType) {
        MoodType.POSITIVE -> MoodPositiveBg
        MoodType.NEUTRAL -> MoodNeutralBg
        MoodType.NEGATIVE -> MoodNegativeBg
        MoodType.MEMO -> MoodMemoBg
    }
}