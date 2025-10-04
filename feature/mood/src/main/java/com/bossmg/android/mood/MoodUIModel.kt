package com.bossmg.android.mood

import androidx.compose.ui.graphics.Color
import com.bossmg.android.designsystem.ui.theme.White
import java.time.LocalDate

internal data class MoodUIModel(
    val moods: Map<String, Int> = emptyMap(),
    val memoItem: List<MemoItem> = emptyList()
)

internal data class MemoItem(
    val id: Int,
    val date: LocalDate,
    val title: String,
    val mood: String,
    val cardColor: Color = White,
    val img: String? = null
)