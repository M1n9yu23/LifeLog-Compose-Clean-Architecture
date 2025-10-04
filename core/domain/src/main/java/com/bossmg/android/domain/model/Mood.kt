package com.bossmg.android.domain.model

import com.bossmg.android.domain.enums.MoodType

data class Mood(
    val str: String,
    val type: MoodType
)