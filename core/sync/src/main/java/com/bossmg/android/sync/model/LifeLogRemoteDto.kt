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
package com.bossmg.android.sync.model

import com.bossmg.android.data.model.LifeLogEntity
import com.bossmg.android.domain.util.MoodProvider

internal data class LifeLogRemoteDto(
    val id: String = "",
    val date: String = "",
    val title: String = "",
    val description: String = "",
    val mood: String = "",
    val updatedAt: Long = 0L,
)

internal fun LifeLogRemoteDto.toEntity() =
    LifeLogEntity(
        id = id,
        date = date,
        title = title,
        description = description,
        mood = sanitizeMood(mood),
        imgs = "",
        updatedAt = updatedAt,
        isSynced = true,
        isDeleted = false,
    )

private val legacyMoodMap =
    mapOf(
        "📝 메모" to MoodProvider.Keys.MEMO,
        "😊 기쁨" to MoodProvider.Keys.JOY,
        "🥰 행복" to MoodProvider.Keys.HAPPY,
        "🤩 설렘" to MoodProvider.Keys.EXCITED,
        "😍 사랑" to MoodProvider.Keys.LOVE,
        "😎 뿌듯함" to MoodProvider.Keys.PROUD,
        "😐 무난함" to MoodProvider.Keys.OKAY,
        "🤔 고민" to MoodProvider.Keys.WORRIED,
        "😴 피곤" to MoodProvider.Keys.TIRED,
        "😢 슬픔" to MoodProvider.Keys.SAD,
        "😡 화남" to MoodProvider.Keys.ANGRY,
        "😰 불안함" to MoodProvider.Keys.ANXIOUS,
        "😞 실망함" to MoodProvider.Keys.DISAPPOINTED,
        "😩 피곤함" to MoodProvider.Keys.EXHAUSTED,
    )

private fun sanitizeMood(raw: String): String = legacyMoodMap[raw] ?: raw
