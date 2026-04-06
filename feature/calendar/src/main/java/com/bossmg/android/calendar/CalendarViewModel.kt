/*
 * Copyright 2026 Gyugle
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bossmg.android.calendar

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bossmg.android.domain.usecase.GetLifeLogsByDateUseCase
import com.bossmg.android.domain.usecase.GetLifeLogsByMonthUseCase
import com.bossmg.android.model.MemoItemMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
internal class CalendarViewModel @Inject constructor(
    private val getLifeLogsByDateUseCase: GetLifeLogsByDateUseCase,
    private val getLifeLogsByMonthUseCase: GetLifeLogsByMonthUseCase,
    private val mapper: MemoItemMapper,
) : ViewModel() {
    private val _selectedDate = MutableStateFlow(LocalDate.now())

    private val _currentMonth = MutableStateFlow(LocalDate.now())

    val uiState: StateFlow<CalendarUiState> =
        combine(
            _selectedDate,
            _currentMonth,
            _currentMonth.flatMapLatest { month ->
                getLifeLogsByMonthUseCase(YearMonth.of(month.year, month.monthValue))
                    .map { logs -> logs.map { it.date }.toImmutableList() }
            },
            _selectedDate.flatMapLatest { date ->
                getLifeLogsByDateUseCase(date)
                    .map { logs -> logs.map { mapper.map(it) }.toImmutableList() }
            },
        ) { selectedDate, currentMonth, markedDates, memoItems ->
            CalendarUiState.Success(
                currentMonth = currentMonth,
                selectedDate = selectedDate,
                markedDates = markedDates,
                memoItems = memoItems,
            ) as CalendarUiState
        }
            .catch { e ->
                Log.e(TAG, "Error in UI State Flow", e)
                emit(CalendarUiState.Error(e.message ?: "알 수 없는 오류가 발생했습니다."))
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = CalendarUiState.Loading,
            )

    fun onDateSelect(date: LocalDate) {
        _selectedDate.value = date
    }

    fun onClickPrevMonth() {
        _currentMonth.update {
            it.minusMonths(1)
        }
    }

    fun onClickNextMonth() {
        _currentMonth.update {
            it.plusMonths(1)
        }
    }

    companion object {
        private const val TAG = "CalendarViewModel"
    }
}
