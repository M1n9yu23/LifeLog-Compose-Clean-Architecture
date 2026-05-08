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

import com.bossmg.android.domain.usecase.GetLifeLogsByMoodUseCase
import com.bossmg.android.domain.usecase.GetLifeLogsUseCase
import com.bossmg.android.domain.util.MoodProvider
import com.bossmg.android.model.MemoItemMapper
import com.bossmg.android.testing.data.lifeLogTestData
import com.bossmg.android.testing.repository.TestLifeLogRepository
import com.bossmg.android.testing.rule.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MoodViewModelTest {
    @get:Rule
    val rule = MainDispatcherRule()

    private lateinit var testRepository: TestLifeLogRepository
    private lateinit var getLifeLogsUseCase: GetLifeLogsUseCase
    private lateinit var getLifeLogsByMoodUseCase: GetLifeLogsByMoodUseCase
    private lateinit var mapper: MemoItemMapper
    private lateinit var viewModel: MoodViewModel

    private val testLifeLogs = lifeLogTestData

    @Before
    fun setUp() {
        testRepository = TestLifeLogRepository()
        getLifeLogsUseCase = GetLifeLogsUseCase(testRepository)
        getLifeLogsByMoodUseCase = GetLifeLogsByMoodUseCase(testRepository)
        mapper = MemoItemMapper()
        viewModel = MoodViewModel(mapper, getLifeLogsByMoodUseCase, getLifeLogsUseCase)
    }

    @Test
    fun uiState_isLoadingInitially_thenPopulatesWithMoodCounts() =
        runTest {
            val job = launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

            assertTrue(viewModel.uiState.value is MoodUIState.Loading)

            testRepository.sendLogs(testLifeLogs)

            val state = viewModel.uiState.value as MoodUIState.Success

            val moodCount = testLifeLogs.groupingBy { it.mood }.eachCount()
            assertEquals(moodCount, state.uiModel.moods)

            job.cancel()
        }

    @Test
    fun selectMood_whenCalled_shouldUpdateSelectedMoodAndFetchFilteredLogs() =
        runTest {
            val job = launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }
            val mood = MoodProvider.Keys.EXCITED

            testRepository.sendLogs(testLifeLogs)
            viewModel.selectMood(mood)

            val state = viewModel.uiState.value as MoodUIState.Success
            assertEquals(mood, state.selectedMood)
            assertTrue(state.uiModel.memoItem.all { it.mood == mood })

            job.cancel()
        }

    @Test
    fun getLifeLogsByMoodUseCase_whenEmitsLogs_shouldMapToUiModelCorrectly() =
        runTest {
            val job = launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }
            val mood = MoodProvider.Keys.JOY
            viewModel.selectMood(mood)

            val filterLogs = testLifeLogs.filter { it.mood == mood }
            testRepository.sendLogs(filterLogs)

            val state = viewModel.uiState.value as MoodUIState.Success
            val memoItems = state.uiModel.memoItem
            assertEquals(filterLogs.size, memoItems.size)

            memoItems.forEachIndexed { index, item ->
                val log = filterLogs[index]
                assertEquals(log.id, item.id)
                assertEquals(log.date, item.date)
                assertEquals(log.title, item.title)
                assertEquals(log.mood, item.mood)
                assertEquals(log.imgs.firstOrNull(), item.img)
            }

            job.cancel()
        }
}
