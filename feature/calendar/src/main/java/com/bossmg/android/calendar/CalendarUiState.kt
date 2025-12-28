package com.bossmg.android.calendar

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.bossmg.android.designsystem.ui.theme.White
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import java.time.LocalDate

@Immutable
internal sealed interface CalendarUiState {
    data object Loading : CalendarUiState

    @Immutable
    data class Success(
        val currentMonth: LocalDate,
        val selectedDate: LocalDate,
        val markedDates: ImmutableList<LocalDate> = persistentListOf(),
        val memoItems: ImmutableList<MemoItem> = persistentListOf()
    ) : CalendarUiState

    @Immutable
    data class Error(
        val message: String
    ) : CalendarUiState
}

@Immutable
internal data class MemoItem(
    val id: Int,
    val date: LocalDate,
    val title: String,
    val mood: String,
    val cardColor: Color = White,
    val img: String? = null
)