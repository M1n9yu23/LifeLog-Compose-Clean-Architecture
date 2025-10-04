package com.bossmg.android.calendar

import androidx.compose.ui.graphics.Color
import com.bossmg.android.designsystem.ui.theme.White
import java.time.LocalDate

internal data class CalendarUIModel(
    val memoItems: List<MemoItem> = emptyList()
)

internal data class MemoItem(
    val id: Int,
    val date: LocalDate,
    val title: String,
    val mood: String,
    val cardColor: Color = White,
    val img: String? = null
)