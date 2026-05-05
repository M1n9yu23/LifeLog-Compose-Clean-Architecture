package com.bossmg.android.designsystem.ui.util

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.bossmg.android.designsystem.R
import com.bossmg.android.domain.util.MoodProvider

@Composable
fun moodLabel(key: String): String = stringResource(moodLabelRes(key))

@StringRes
fun moodLabelRes(key: String): Int =
    when (key) {
        MoodProvider.Keys.MEMO -> R.string.mood_label_memo
        MoodProvider.Keys.JOY -> R.string.mood_label_joy
        MoodProvider.Keys.HAPPY -> R.string.mood_label_happy
        MoodProvider.Keys.EXCITED -> R.string.mood_label_excited
        MoodProvider.Keys.LOVE -> R.string.mood_label_love
        MoodProvider.Keys.PROUD -> R.string.mood_label_proud
        MoodProvider.Keys.OKAY -> R.string.mood_label_okay
        MoodProvider.Keys.WORRIED -> R.string.mood_label_worried
        MoodProvider.Keys.TIRED -> R.string.mood_label_tired
        MoodProvider.Keys.SAD -> R.string.mood_label_sad
        MoodProvider.Keys.ANGRY -> R.string.mood_label_angry
        MoodProvider.Keys.ANXIOUS -> R.string.mood_label_anxious
        MoodProvider.Keys.DISAPPOINTED -> R.string.mood_label_disappointed
        MoodProvider.Keys.EXHAUSTED -> R.string.mood_label_exhausted
        else -> R.string.mood_label_memo
    }
