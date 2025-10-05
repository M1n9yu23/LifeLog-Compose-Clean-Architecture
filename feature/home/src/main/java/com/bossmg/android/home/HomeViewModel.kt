package com.bossmg.android.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bossmg.android.domain.usecase.GetLifeLogsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
internal class HomeViewModel @Inject constructor(
    private val getLifeLogsUseCase: GetLifeLogsUseCase,
    private val mapper: HomeMapper
) : ViewModel() {

    val uiState: StateFlow<HomeUIState> = getLifeLogsUseCase()
        .map { lifeLogs ->
            val uiModels = lifeLogs.map { mapper.map(it) }
            HomeUIState.Success(uiModels)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeUIState.Loading
        )

}

internal sealed interface HomeUIState {
    data object Loading : HomeUIState
    data class Success(
        val uiModels: List<HomeUIModel> = emptyList()
    ) : HomeUIState
}