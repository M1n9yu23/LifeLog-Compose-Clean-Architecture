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
package com.bossmg.android.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bossmg.android.common.di.DefaultDispatcher
import com.bossmg.android.domain.usecase.GetLifeLogsUseCase
import com.bossmg.android.domain.usecase.SearchLifeLogsUseCase
import com.bossmg.android.domain.usecase.base.invoke
import com.bossmg.android.model.MemoItem
import com.bossmg.android.model.MemoItemMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
internal class HomeViewModel @Inject constructor(
    private val getLifeLogsUseCase: GetLifeLogsUseCase,
    private val searchLifeLogsUseCase: SearchLifeLogsUseCase,
    private val mapper: MemoItemMapper,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
) : ViewModel() {
    val uiState: StateFlow<HomeUIState> =
        getLifeLogsUseCase()
            .map { lifeLogs ->
                HomeUIState.Success(lifeLogs.map { mapper.map(it) })
            }
            .flowOn(defaultDispatcher)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = HomeUIState.Loading,
            )

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val searchResults: StateFlow<List<MemoItem>> =
        _searchQuery
            .debounce(100L)
            .flatMapLatest { query ->
                flow {
                    val results = searchLifeLogsUseCase(query).map { mapper.map(it) }
                    emit(results)
                }.flowOn(defaultDispatcher)
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
        val uiModels: List<MemoItem> = emptyList(),
    ) : HomeUIState
}
