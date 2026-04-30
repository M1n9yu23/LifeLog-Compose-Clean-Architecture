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
package com.bossmg.android.memo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bossmg.android.domain.usecase.DeleteLifeLogByIdUseCase
import com.bossmg.android.domain.usecase.GetLifeLogByIdUseCase
import com.bossmg.android.domain.usecase.InsertLifeLogUseCase
import com.bossmg.android.domain.usecase.UpsertLifeLogUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
internal class MemoViewModel @Inject constructor(
    private val mapper: MemoMapper,
    private val getLifeLogByIdUseCase: GetLifeLogByIdUseCase,
    private val insertLifeLogUseCase: InsertLifeLogUseCase,
    private val upsertLifeLogUseCase: UpsertLifeLogUseCase,
    private val deleteLifeLogByIdUseCase: DeleteLifeLogByIdUseCase,
) : ViewModel() {
    private val _uiModel = MutableStateFlow(MemoUIModel())
    val uiModel = _uiModel.asStateFlow()

    private val _events = Channel<MemoEvent>(Channel.BUFFERED)
    val events: Flow<MemoEvent> = _events.receiveAsFlow()

    fun load(id: String?) {
        if (id == null) {
            _uiModel.value = MemoUIModel()
        } else {
            viewModelScope.launch {
                try {
                    val lifeLog = getLifeLogByIdUseCase(id)
                    _uiModel.value = mapper.map(lifeLog)
                } catch (_: Exception) {
                    _uiModel.value = MemoUIModel()
                }
            }
        }
    }

    fun updateTitle(title: String) =
        _uiModel.update { it.copy(title = title) }

    fun updateDescription(description: String) =
        _uiModel.update { it.copy(description = description) }

    fun updateDate(date: LocalDate) =
        _uiModel.update { it.copy(selectedDate = date) }

    fun updateMood(mood: String) =
        _uiModel.update { it.copy(selectedMood = mood) }

    fun addImage(uri: String) =
        _uiModel.update { it.copy(imgs = it.imgs + uri) }

    fun addImages(uris: List<String>) =
        _uiModel.update { it.copy(imgs = it.imgs + uris) }

    fun removeImage(uri: String) =
        _uiModel.update { it.copy(imgs = it.imgs - uri) }

    fun saveMemo() {
        viewModelScope.launch {
            val isNew = _uiModel.value.id.isEmpty()
            val lifeLog = mapper.mapBack(_uiModel.value)
            upsertLifeLogUseCase(lifeLog)
            _events.send(if (isNew) MemoEvent.MemoAdded else MemoEvent.MemoEdited)
        }
    }

    fun deleteMemo(id: String?) {
        id?.let {
            viewModelScope.launch {
                deleteLifeLogByIdUseCase(it)
                _events.send(MemoEvent.MemoDeleted)
            }
        }
    }
}
