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
        "📝 메모" to "메모",
        "😊 기쁨" to "기쁨",
        "🥰 행복" to "행복",
        "🤩 설렘" to "설렘",
        "😍 사랑" to "사랑",
        "😎 뿌듯함" to "뿌듯함",
        "😐 무난함" to "무난함",
        "🤔 고민" to "고민",
        "😴 피곤" to "피곤",
        "😢 슬픔" to "슬픔",
        "😡 화남" to "화남",
        "😰 불안함" to "불안함",
        "😞 실망함" to "실망함",
        "😩 피곤함" to "피곤함",
    )

private fun sanitizeMood(raw: String): String = legacyMoodMap[raw] ?: raw
