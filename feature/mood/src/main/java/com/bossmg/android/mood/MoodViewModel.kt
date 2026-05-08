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
package com.bossmg.android.mood

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bossmg.android.domain.usecase.GetLifeLogsByMoodUseCase
import com.bossmg.android.domain.usecase.GetLifeLogsUseCase
import com.bossmg.android.domain.usecase.base.invoke
import com.bossmg.android.model.MemoItemMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
internal class MoodViewModel @Inject constructor(
    private val mapper: MemoItemMapper,
    private val getLifeLogsByMoodUseCase: GetLifeLogsByMoodUseCase,
    private val getLifeLogsUseCase: GetLifeLogsUseCase,
) : ViewModel() {
    private val _selectedMood = MutableStateFlow("")

    val uiState: StateFlow<MoodUIState> =
        combine(
            getLifeLogsUseCase(),
            _selectedMood.flatMapLatest { mood ->
                if (mood.isEmpty()) {
                    flowOf(emptyList())
                } else {
                    getLifeLogsByMoodUseCase(mood)
                }
            },
            _selectedMood,
        ) { allLogs, filteredLogs, selectedMood ->
            MoodUIState.Success(
                uiModel =
                    MoodUIModel(
                        moods = allLogs.map { mapper.map(it) }.groupingBy { it.mood }.eachCount(),
                        memoItem = filteredLogs.map { mapper.map(it) },
                    ),
                selectedMood = selectedMood,
            )
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = MoodUIState.Loading,
            )

    fun selectMood(mood: String) {
        if (_selectedMood.value != mood) {
            _selectedMood.value = mood
        }
    }
}

internal sealed interface MoodUIState {
    data object Loading : MoodUIState

    data class Success(
        val uiModel: MoodUIModel,
        val selectedMood: String,
    ) : MoodUIState
}
