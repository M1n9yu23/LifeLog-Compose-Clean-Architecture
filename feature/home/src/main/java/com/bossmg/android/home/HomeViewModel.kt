package com.bossmg.android.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bossmg.android.domain.usecase.GetLifeLogsUseCase
import com.bossmg.android.domain.usecase.SearchLifeLogsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
internal class HomeViewModel @Inject constructor(
    private val getLifeLogsUseCase: GetLifeLogsUseCase,
    private val searchLifeLogsUseCase: SearchLifeLogsUseCase,
    private val mapper: HomeMapper,
) : ViewModel() {
    val uiState: StateFlow<HomeUIState> =
        getLifeLogsUseCase()
            .map { lifeLogs ->
                HomeUIState.Success(lifeLogs.map { mapper.map(it) })
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = HomeUIState.Loading,
            )

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val searchResults: StateFlow<List<HomeUIModel>> =
        _searchQuery
            .debounce(300L)
            .flatMapLatest { query ->
                flow {
                    val results = searchLifeLogsUseCase(query).map { mapper.map(it) }
                    emit(results)
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList(),
            )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }
}

internal sealed interface HomeUIState {
    data object Loading : HomeUIState

    data class Success(
        val uiModels: List<HomeUIModel> = emptyList(),
    ) : HomeUIState
}
