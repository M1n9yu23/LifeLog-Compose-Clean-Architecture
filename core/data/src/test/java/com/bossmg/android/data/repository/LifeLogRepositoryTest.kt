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
package com.bossmg.android.data.repository

import com.bossmg.android.data.mapper.LifeLogMapper
import com.bossmg.android.data.model.LifeLogEntity
import com.bossmg.android.domain.model.LifeLog
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class LifeLogRepositoryTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private val scope = TestScope(testDispatcher)

    private lateinit var dao: TestLifeLogDao
    private lateinit var mapper: LifeLogMapper
    private lateinit var repository: LifeLogRepositoryImpl

    private val dummyLogs =
        listOf(
            LifeLogEntity(1, "2025-10-01", "아침 산책", "공원에서 산책", "\uD83D\uDE0A 기쁨", "산책.jpg"),
            LifeLogEntity(2, "2025-10-02", "새 프로젝트 시작", "안드로이드 프로젝트 시작", "\uD83E\uDD29 설렘"),
            LifeLogEntity(3, "2025-10-02", "친구와 저녁", "오랜만에 친구와 저녁식사", "\uD83E\uDD70 행복", "음식.jpg"),
        )

    @Before
    fun setUp() =
        runTest {
            dao = TestLifeLogDao()
            mapper = LifeLogMapper()
            repository = LifeLogRepositoryImpl(dao, mapper, FakeSearchEngine(), testDispatcher)

            dummyLogs.forEach {
                dao.insertLifeLog(it)
            }
        }

    @Test
    fun getLifeLogs_returnsMappedValues_fromDaoFlow() =
        scope.runTest {
            val result = repository.getLifeLogs().first()
            assertEquals(3, result.size)
            assertEquals("아침 산책", result.find { it.title == "아침 산책" }?.title)
        }

    @Test
    fun givenDate_whenGetLifeLogsByDate_thenReturnsFilteredList() =
        scope.runTest {
            val result = repository.getLifeLogsByDate(LocalDate.of(2025, 10, 2)).first()
            assertEquals(2, result.size)
        }

    @Test
    fun givenMood_whenGetLifeLogsByMood_thenReturnsFilteredList() =
        scope.runTest {
            val result = repository.getLifeLogsByMood("\uD83D\uDE0A 기쁨").first()
            assertEquals(1, result.size)
        }

    @Test
    fun givenId_whenGetLifeLogById_thenReturnsCorrectLog() =
        scope.runTest {
            val result = repository.getLifeLogById(3)
            assertEquals("친구와 저녁", result.title)
            assertEquals(LocalDate.of(2025, 10, 2), result.date)
            assertEquals("\uD83E\uDD70 행복", result.mood)
            assertEquals("오랜만에 친구와 저녁식사", result.description)
            assertEquals(3, result.id)
        }

    @Test
    fun givenNewLog_whenInsert_thenAddedNewLog() =
        scope.runTest {
            val newLog =
                LifeLog(
                    id = 4,
                    date = LocalDate.of(2025, 10, 3),
                    title = "저녁 독서",
                    description = "책 읽기",
                    mood = "\uD83D\uDCDD 메모",
                    img = "book.jpg",
                )
            repository.insertLifeLog(newLog)

            val logs = repository.getLifeLogs().first()
            assertEquals(4, logs.size)
            assertEquals(newLog.id, logs.last().id)
            assertEquals(newLog.title, logs.last().title)
            assertEquals(newLog.date, logs.last().date)
            assertEquals(newLog.description, logs.last().description)
            assertEquals(newLog.mood, logs.last().mood)
            assertEquals(newLog.img, logs.last().img)
        }

    @Test
    fun givenNewLog_whenUpsert_thenInsertsLog() =
        scope.runTest {
            val newLog =
                LifeLog(
                    id = 5,
                    date = LocalDate.of(2025, 10, 4),
                    title = "새 기록",
                    description = "업데이트가 아닌 새로운 기록",
                    mood = "\uD83D\uDE0A 기쁨",
                )

            repository.upsertLifeLog(newLog)

            val logs = repository.getLifeLogs().first()

            assertEquals(4, logs.size)
            assertEquals(newLog.id, logs.last().id)
            assertEquals(newLog.title, logs.last().title)
            assertEquals(newLog.date, logs.last().date)
            assertEquals(newLog.description, logs.last().description)
            assertEquals(newLog.mood, logs.last().mood)
            assertEquals(newLog.img, logs.last().img)
        }

    @Test
    fun givenExistingLog_whenUpsert_thenUpdatesLog() =
        scope.runTest {
            val updatedLog =
                LifeLog(
                    id = 1,
                    date = LocalDate.of(2025, 10, 1),
                    title = "id 1 수정",
                    description = "축구를 했다.",
                    mood = "\uD83D\uDE0A 기쁨",
                    img = "축구.jpg",
                )

            repository.upsertLifeLog(updatedLog)

            val logs = repository.getLifeLogs().first()
            val log = logs.first { it.id == 1 }

            assertEquals(3, logs.size)
            assertEquals("id 1 수정", log.title)
            assertEquals("축구를 했다.", log.description)
            assertEquals("축구.jpg", log.img)
            assertEquals("\uD83D\uDE0A 기쁨", log.mood)

            assertEquals(0, logs.indexOf(log))
        }

    @Test
    fun getImages_returnsOnlyNonNullImages() =
        scope.runTest {
            val images = repository.getImages().first()

            assertEquals(2, images.size)
            assertEquals("산책.jpg", images[0])
            assertEquals("음식.jpg", images[1])
        }

    @Test
    fun givenQuery_whenSearchLifeLogs_thenReturnsMatchingLogs() =
        scope.runTest {
            dummyLogs.forEach { entity ->
                repository.insertLifeLog(mapper.map(entity))
            }

            val result = repository.searchLifeLogs("산책")

            assertEquals(1, result.size)
            assertEquals("아침 산책", result.first().title)
        }

    @Test
    fun givenBlankQuery_whenSearchLifeLogs_thenReturnsEmpty() =
        scope.runTest {
            val result = repository.searchLifeLogs("  ")
            assertEquals(0, result.size)
        }
}
