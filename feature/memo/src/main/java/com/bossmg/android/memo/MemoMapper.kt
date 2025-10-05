package com.bossmg.android.memo

import com.bossmg.android.domain.mapper.BiMapper
import com.bossmg.android.domain.model.LifeLog
import java.time.LocalDate
import javax.inject.Inject

internal class MemoMapper @Inject constructor() : BiMapper<LifeLog, MemoUIModel> {
    override fun mapBack(output: MemoUIModel): LifeLog = LifeLog(
        id = output.id,
        date = output.selectedDate.toString(),
        title = output.title,
        description = output.description,
        mood = output.selectedMood,
        img = output.img
    )

    override fun map(input: LifeLog): MemoUIModel = MemoUIModel(
        id = input.id,
        title = input.title,
        description = input.description,
        selectedDate = LocalDate.parse(input.date),
        selectedMood = input.mood,
        img = input.img
    )
}