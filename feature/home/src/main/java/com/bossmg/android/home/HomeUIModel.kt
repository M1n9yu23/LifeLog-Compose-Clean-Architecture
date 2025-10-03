package com.bossmg.android.home

import androidx.compose.ui.graphics.Color
import com.bossmg.android.designsystem.ui.theme.White
import java.time.LocalDate

internal data class HomeUIModel(
    val id: Int,
    val date: LocalDate,
    val title: String,
    val mood: String,
    val cardColor: Color = White,
    val img: String? = null
)