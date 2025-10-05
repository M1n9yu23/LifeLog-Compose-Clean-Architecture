package com.bossmg.android.domain.model

import com.bossmg.android.domain.util.MoodProvider
import java.time.LocalDate

data class LifeLog(
    val id: Int = 0,
    val date: String = LocalDate.now().toString(),
    val title: String = "",
    val description: String = "",
    val mood: String = MoodProvider.Moods.first().str,
    val img: String? = null
)
