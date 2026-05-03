package com.bossmg.android.memo.camera

internal sealed interface CameraEvent {
    data class PhotoConfirmed(val uri: String) : CameraEvent
    data object ConfirmFailed : CameraEvent
}
