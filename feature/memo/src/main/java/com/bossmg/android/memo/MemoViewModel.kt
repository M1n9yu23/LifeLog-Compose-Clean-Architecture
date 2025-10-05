package com.bossmg.android.memo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bossmg.android.domain.usecase.DeleteLifeLogByIdUseCase
import com.bossmg.android.domain.usecase.GetLifeLogByIdUseCase
import com.bossmg.android.domain.usecase.InsertLifeLogUseCase
import com.bossmg.android.domain.usecase.UpsertLifeLogUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    private val deleteLifeLogByIdUseCase: DeleteLifeLogByIdUseCase
) : ViewModel() {

    private val _uiModel = MutableStateFlow(MemoUIModel())
    val uiModel = _uiModel.asStateFlow()

    fun load(id: Int?) {
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

    fun updateImage(img: String?) =
        _uiModel.update { it.copy(img = img) }

    fun saveMemo() {
        viewModelScope.launch {
            val lifeLog = mapper.mapBack(_uiModel.value)
            upsertLifeLogUseCase(lifeLog)
        }
    }

    fun deleteMemo(id: Int?) {
        id?.let {
            viewModelScope.launch {
                deleteLifeLogByIdUseCase(it)
            }
        }
    }
}