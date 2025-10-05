package com.bossmg.android.home

import com.bossmg.android.designsystem.ui.util.cardColor
import com.bossmg.android.domain.mapper.Mapper
import com.bossmg.android.domain.model.LifeLog
import java.time.LocalDate
import javax.inject.Inject

internal class HomeMapper @Inject constructor() : Mapper<LifeLog, HomeUIModel> {
    override fun map(input: LifeLog): HomeUIModel = HomeUIModel(
        id = input.id,
        date = LocalDate.parse(input.date),
        title = input.title,
        mood = input.mood,
        cardColor = cardColor(input.mood),
        img = input.img
    )
}