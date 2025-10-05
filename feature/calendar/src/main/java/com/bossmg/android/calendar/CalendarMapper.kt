package com.bossmg.android.calendar

import com.bossmg.android.designsystem.ui.util.cardColor
import com.bossmg.android.domain.mapper.Mapper
import com.bossmg.android.domain.model.LifeLog
import java.time.LocalDate
import javax.inject.Inject

internal class CalendarMapper @Inject constructor() : Mapper<LifeLog, MemoItem> {
    override fun map(input: LifeLog): MemoItem = MemoItem(
        id = input.id,
        date = LocalDate.parse(input.date),
        title = input.title,
        mood = input.mood,
        cardColor = cardColor(input.mood),
        img = input.img
    )
}