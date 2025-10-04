package com.bossmg.android.memo

import java.time.LocalDate

internal data class MemoUIModel(
    val memoItem: MemoItem,
)

internal data class MemoItem(
    val title: String ="",
    val description: String = "",
    val selectedDate: LocalDate = LocalDate.now(),
    val selectedMood: String = "\uD83D\uDE0A 기쁨",
    val img: String? = null
)