package com.bossmg.android.memo.camera

import androidx.camera.core.SurfaceRequest
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
internal class CameraViewModel @Inject constructor(
    private val captureController: CaptureController,
) : ViewModel() {
    private val _surfaceRequest = MutableStateFlow<SurfaceRequest?>(null)
    val surfaceRequest: StateFlow<SurfaceRequest?> = _surfaceRequest.asStateFlow()

    private val _captureState = MutableStateFlow<CaptureState>(CaptureState.Idle)
    val captureState: StateFlow<CaptureState> = _captureState.asStateFlow()

    private val _events = Channel<CameraEvent>(Channel.BUFFERED)
    val events: Flow<CameraEvent> = _events.receiveAsFlow()

    init {
        captureController.preview.setSurfaceProvider { newSurfaceRequest ->
            _surfaceRequest.value = newSurfaceRequest
        }
    }

    fun startCamera(lifecycleOwner: LifecycleOwner) {
        viewModelScope.launch {
            captureController.bindToLifecycle(lifecycleOwner)
        }
    }

    fun capturePhoto() {
        if (_captureState.value is CaptureState.Capturing) return
        viewModelScope.launch {
            _captureState.update { CaptureState.Capturing }
            _captureState.update {
                captureController.capturePhoto().fold(
                    onSuccess = { CaptureState.Preview(it) },
                    onFailure = { CaptureState.Failure(it) },
                )
            }
        }
    }

    fun confirmCapture() {
        val absolutePath = (_captureState.value as? CaptureState.Preview)?.absolutePath ?: return
        viewModelScope.launch {
            _captureState.update { CaptureState.Idle }
            captureController.confirmCapture(absolutePath)
                .onSuccess { uri -> _events.send(CameraEvent.PhotoConfirmed(uri)) }
                .onFailure { _events.send(CameraEvent.ConfirmFailed) }
        }
    }

    fun resetCaptureState() {
        val absolutePath = (_captureState.value as? CaptureState.Preview)?.absolutePath
        _captureState.update { CaptureState.Idle }
        absolutePath?.let { viewModelScope.launch { File(it).delete() } }
    }
}
