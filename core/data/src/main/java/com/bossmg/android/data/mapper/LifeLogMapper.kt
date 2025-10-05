package com.bossmg.android.data.mapper

import com.bossmg.android.data.model.LifeLogEntity
import com.bossmg.android.domain.mapper.BiMapper
import com.bossmg.android.domain.model.LifeLog
import javax.inject.Inject

class LifeLogMapper @Inject constructor() : BiMapper<LifeLogEntity, LifeLog> {
    override fun mapBack(output: LifeLog): LifeLogEntity = LifeLogEntity(
        id = output.id,
        title = output.title,
        description = output.description,
        mood = output.mood,
        date = output.date,
        img = output.img
    )

    override fun map(input: LifeLogEntity): LifeLog = LifeLog(
        id = input.id,
        title = input.title,
        description = input.description,
        mood = input.mood,
        date = input.date,
        img = input.img
    )
}