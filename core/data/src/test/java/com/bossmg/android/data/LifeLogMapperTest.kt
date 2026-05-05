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
package com.bossmg.android.data

import com.bossmg.android.data.mapper.LifeLogMapper
import com.bossmg.android.data.model.LifeLogEntity
import com.bossmg.android.domain.model.LifeLog
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class LifeLogMapperTest {
    private lateinit var mapper: LifeLogMapper

    @Before
    fun setUp() {
        mapper = LifeLogMapper()
    }

    @Test
    fun mapEntityToDomain() {
        val entity =
            LifeLogEntity(
                id = "1",
                date = "2025-10-05",
                title = "Test Title",
                description = "Test Description",
                mood = "기쁨",
                imgs = "image.png",
            )

        val domainModel = mapper.map(entity)

        assertEquals(entity.id, domainModel.id)
        assertEquals(entity.title, domainModel.title)
        assertEquals(LocalDate.parse(entity.date), domainModel.date)
        assertEquals(entity.mood, domainModel.mood)
        assertEquals(entity.description, domainModel.description)
        assertEquals(listOf("image.png"), domainModel.imgs)
    }

    @Test
    fun mapBackDomainToEntity() {
        val domainModel =
            LifeLog(
                id = "2",
                date = LocalDate.of(2025, 10, 6),
                title = "Domain Title",
                description = "Domain Description",
                mood = "슬픔",
                imgs = listOf("test.png"),
            )

        val entity = mapper.mapBack(domainModel)

        assertEquals(domainModel.id, entity.id)
        assertEquals(domainModel.title, entity.title)
        assertEquals(domainModel.date.toString(), entity.date)
        assertEquals(domainModel.mood, entity.mood)
        assertEquals(domainModel.description, entity.description)
        assertEquals("test.png", entity.imgs)
    }

    @Test
    fun mapEntityToDomain_multipleImages() {
        val entity =
            LifeLogEntity(
                id = "3",
                date = "2025-10-07",
                title = "Multi Image",
                description = "여러 이미지 테스트",
                mood = "기쁨",
                imgs = "a.jpg|b.jpg",
            )

        val domainModel = mapper.map(entity)

        assertEquals(listOf("a.jpg", "b.jpg"), domainModel.imgs)
    }

    @Test
    fun mapBack_multipleImages() {
        val domainModel =
            LifeLog(
                id = "4",
                date = LocalDate.of(2025, 10, 8),
                title = "Multi Image Back",
                description = "여러 이미지 역매핑 테스트",
                mood = "슬픔",
                imgs = listOf("a.jpg", "b.jpg"),
            )

        val entity = mapper.mapBack(domainModel)

        assertEquals("a.jpg|b.jpg", entity.imgs)
    }

    @Test
    fun mapEntityToDomain_emptyImgs_returnsEmptyList() {
        val entity =
            LifeLogEntity(
                id = "5",
                date = "2025-10-09",
                title = "No Image",
                description = "이미지 없음",
                mood = "무난함",
                imgs = "",
            )

        val domainModel = mapper.map(entity)

        assertEquals(emptyList<String>(), domainModel.imgs)
    }

    @Test
    fun mapBack_emptyImgs_returnsEmptyString() {
        val domainModel =
            LifeLog(
                id = "6",
                date = LocalDate.of(2025, 10, 10),
                title = "No Image Back",
                description = "이미지 없음 역매핑",
                mood = "무난함",
                imgs = emptyList(),
            )

        val entity = mapper.mapBack(domainModel)

        assertEquals("", entity.imgs)
    }
}
