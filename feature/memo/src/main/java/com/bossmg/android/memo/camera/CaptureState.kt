package com.bossmg.android.memo.camera

internal sealed interface CaptureState {
    data object Idle : CaptureState
    data object Capturing : CaptureState
    data class Preview(val absolutePath: String) : CaptureState
    data class Failure(val throwable: Throwable) : CaptureState
}
