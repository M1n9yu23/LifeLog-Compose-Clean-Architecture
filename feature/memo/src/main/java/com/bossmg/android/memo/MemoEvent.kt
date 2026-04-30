package com.bossmg.android.memo

internal sealed interface MemoEvent {
    data object MemoAdded : MemoEvent

    data object MemoEdited : MemoEvent

    data object MemoDeleted : MemoEvent
}
