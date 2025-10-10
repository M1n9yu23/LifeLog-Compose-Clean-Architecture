package com.bossmg.android.mood

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bossmg.android.domain.usecase.GetLifeLogsByMoodUseCase
import com.bossmg.android.domain.usecase.GetLifeLogsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class MoodViewModel @Inject constructor(
    private val mapper: MoodMapper,
    private val getLifeLogsByMoodUseCase: GetLifeLogsByMoodUseCase,
    private val getLifeLogsUseCase: GetLifeLogsUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(MoodUIState())
    val uiState = _uiState.asStateFlow()

    fun load() {
        _uiState.update {
            it.copy(
                isLoading = true
            )
        }
        viewModelScope.launch {
            getLifeLogsUseCase().collectLatest { logs ->
                val items = logs.map { mapper.map(it) }
                val moods: Map<String, Int> = items.groupingBy { it.mood }.eachCount()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        uiModel = it.uiModel.copy(
                            moods = moods
                        )
                    )
                }
            }
        }
    }

    fun selectMood(mood: String) {
        if (_uiState.value.selectedMood != mood) {
            _uiState.update {
                it.copy(
                    selectedMood = mood
                )
            }

            getLifeLogsByMood()
        }
    }

    private fun getLifeLogsByMood() {
        _uiState.update {
            it.copy(
                isLoading = true
            )
        }
        viewModelScope.launch {
            getLifeLogsByMoodUseCase(_uiState.value.selectedMood).collectLatest { logs ->
                val items = logs.map { mapper.map(it) }
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        uiModel = it.uiModel.copy(
                            memoItem = items
                        )
                    )
                }
            }
        }
    }
}

internal data class MoodUIState(
    val isLoading: Boolean = false,
    val uiModel: MoodUIModel = MoodUIModel(),
    val selectedMood: String = ""
)