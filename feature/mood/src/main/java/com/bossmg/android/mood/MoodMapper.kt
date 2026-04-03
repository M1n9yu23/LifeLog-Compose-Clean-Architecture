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
package com.bossmg.android.mood

import com.bossmg.android.designsystem.ui.util.cardColor
import com.bossmg.android.domain.mapper.Mapper
import com.bossmg.android.domain.model.LifeLog
import java.time.LocalDate
import javax.inject.Inject

internal class MoodMapper @Inject constructor() : Mapper<LifeLog, MemoItem> {
    override fun map(input: LifeLog): MemoItem =
        MemoItem(
            id = input.id,
            date = LocalDate.parse(input.date),
            title = input.title,
            mood = input.mood,
            cardColor = cardColor(input.mood),
            img = input.img,
        )
}
