package com.bossmg.android.photo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bossmg.android.domain.usecase.GetImagesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
internal class PhotoViewModel(
    private val getImagesUseCase: GetImagesUseCase
) : ViewModel() {

    val uiState: StateFlow<PhotoUIState> = getImagesUseCase().map { imgs ->
        PhotoUIState.Success(PhotoUIModel(imgs))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = PhotoUIState.Loading
    )

}

internal sealed interface PhotoUIState {
    object Loading : PhotoUIState
    data class Success(
        val uiModel: PhotoUIModel
    ) : PhotoUIState
}