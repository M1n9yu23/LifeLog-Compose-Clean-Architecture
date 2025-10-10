package com.bossmg.android.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bossmg.android.domain.usecase.GetLifeLogsByDateUseCase
import com.bossmg.android.domain.usecase.GetLifeLogsByMonthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
internal class CalendarViewModel @Inject constructor(
    private val getLifeLogsByDateUseCase: GetLifeLogsByDateUseCase,
    private val getLifeLogsByMonthUseCase: GetLifeLogsByMonthUseCase,
    private val mapper: CalendarMapper
) : ViewModel() {

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    private val _currentMonth = MutableStateFlow(LocalDate.now())
    val currentMonth: StateFlow<LocalDate> = _currentMonth.asStateFlow()

    val markedDate: StateFlow<Set<LocalDate>> = _currentMonth.flatMapLatest { date ->
        getLifeLogsByMonthUseCase(date.year, date.monthValue).map { logs ->
            logs.map { LocalDate.parse(it.date) }.toSet()
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptySet()
    )

    val uiState: StateFlow<CalendarUIState> = selectedDate.flatMapLatest { date ->
        getLifeLogsByDateUseCase(date.toString()).map { lifeLogs ->
            val uiModel = CalendarUIModel(
                memoItems = lifeLogs.map { mapper.map(it) }
            )
            CalendarUIState.Success(uiModel)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = CalendarUIState.Loading
    )

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
    }

    fun onPrevMonth() {
        _currentMonth.update {
            it.minusMonths(1)
        }
    }

    fun onNextMonth() {
        _currentMonth.update {
            it.plusMonths(1)
        }
    }
}

internal sealed interface CalendarUIState {
    object Loading : CalendarUIState
    data class Success(
        val uiModel: CalendarUIModel
    ) : CalendarUIState
}