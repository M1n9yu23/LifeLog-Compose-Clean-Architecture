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
package com.bossmg.android.home

import com.bossmg.android.domain.model.LifeLog
import com.bossmg.android.domain.usecase.GetLifeLogsUseCase
import com.bossmg.android.domain.usecase.SearchLifeLogsUseCase
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
class HomeViewModelTest {
    @get:Rule
    val rule = MainDispatcherRule()

    private lateinit var testLifeLogRepository: TestLifeLogRepository
    private lateinit var getLifeLogsUseCase: GetLifeLogsUseCase
    private lateinit var searchLifeLogsUseCase: SearchLifeLogsUseCase
    private lateinit var mapper: MemoItemMapper
    private lateinit var viewModel: HomeViewModel

    private val testLifeLogs: List<LifeLog> = lifeLogTestData

    @Before
    fun setUp() {
        testLifeLogRepository = TestLifeLogRepository()
        getLifeLogsUseCase = GetLifeLogsUseCase(testLifeLogRepository)
        searchLifeLogsUseCase = SearchLifeLogsUseCase(testLifeLogRepository)
        mapper = MemoItemMapper()
        viewModel = HomeViewModel(getLifeLogsUseCase, searchLifeLogsUseCase, mapper)
    }

    @Test
    fun uiState_shouldLoadingInit() {
        assertEquals(HomeUIState.Loading, viewModel.uiState.value)
    }

    @Test
    fun uiState_shouldEmitSuccess_whenRepositoryHasData() =
        runTest {
            val job = launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

            testLifeLogRepository.sendLogs(testLifeLogs)

            val state = viewModel.uiState.value
            assertTrue(state is HomeUIState.Success)
            assertEquals(testLifeLogs.size, (state as HomeUIState.Success).uiModels.size)
            assertEquals(testLifeLogs[0].title, state.uiModels[0].title)

            job.cancel()
        }

    @Test
    fun uiState_shouldUpdate_whenRepositoryEmitsNewData() =
        runTest {
            val job = launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

            testLifeLogRepository.sendLogs(listOf(testLifeLogs[0]))
            var state = viewModel.uiState.value
            assertEquals(1, (state as HomeUIState.Success).uiModels.size)

            testLifeLogRepository.sendLogs(testLifeLogs)
            state = viewModel.uiState.value
            assertEquals(testLifeLogs.size, (state as HomeUIState.Success).uiModels.size)

            job.cancel()
        }

    @Test
    fun uiState_shouldMapLifeLogToMemoItemCorrectly() =
        runTest {
            val job = launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

            testLifeLogRepository.sendLogs(testLifeLogs)
            val state = viewModel.uiState.value
            assertTrue(state is HomeUIState.Success)
            val successState = state as HomeUIState.Success

            successState.uiModels.forEachIndexed { index, uiModel ->
                val lifeLog = testLifeLogs[index]
                assertEquals(lifeLog.id, uiModel.id)
                assertEquals(lifeLog.date, uiModel.date)
                assertEquals(lifeLog.title, uiModel.title)
                assertEquals(lifeLog.mood, uiModel.mood)
                assertEquals(lifeLog.imgs.firstOrNull(), uiModel.img)
            }

            job.cancel()
        }
}
