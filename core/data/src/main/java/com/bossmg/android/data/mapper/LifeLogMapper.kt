/*
 * Copyright 2026 Gyugle
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bossmg.android.data.mapper

import com.bossmg.android.data.model.LifeLogEntity
import com.bossmg.android.domain.mapper.BiMapper
import com.bossmg.android.domain.model.LifeLog
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject

class LifeLogMapper @Inject constructor() : BiMapper<LifeLogEntity, LifeLog> {
    override fun mapBack(output: LifeLog): LifeLogEntity =
        LifeLogEntity(
            id = output.id.ifEmpty { UUID.randomUUID().toString() },
            title = output.title,
            description = output.description,
            mood = output.mood,
            date = output.date.toString(),
            imgs = output.imgs.joinToString("|"),
        )

    override fun map(input: LifeLogEntity): LifeLog =
        LifeLog(
            id = input.id,
            title = input.title,
            description = input.description,
            mood = input.mood,
            date = LocalDate.parse(input.date),
            imgs = input.imgs.split("|").filter { it.isNotEmpty() },
        )
}
