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
package com.bossmg.android.photo

import com.bossmg.android.domain.model.LifeLog
import com.bossmg.android.domain.usecase.GetImagesUseCase
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
class PhotoViewModelTest {
    @get:Rule
    val rule = MainDispatcherRule()

    private lateinit var testLifeLogRepository: TestLifeLogRepository
    private lateinit var getImagesUseCase: GetImagesUseCase
    private lateinit var viewModel: PhotoViewModel

    private val testLifeLogs: List<LifeLog> = lifeLogTestData

    @Before
    fun setUp() {
        testLifeLogRepository = TestLifeLogRepository()
        getImagesUseCase = GetImagesUseCase(testLifeLogRepository)
        viewModel = PhotoViewModel(getImagesUseCase)
    }

    @Test
    fun uiState_shouldLoadingInit() {
        assertEquals(PhotoUIState.Loading, viewModel.uiState.value)
    }

    @Test
    fun uiState_shouldEmitSuccess_whenRepositoryHasData() =
        runTest {
            val job = launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

            testLifeLogRepository.sendLogs(testLifeLogs)

            val state = viewModel.uiState.value
            val testImages = testLifeLogs.flatMap { it.imgs }

            assertTrue(state is PhotoUIState.Success)
            assertEquals(testImages, (state as PhotoUIState.Success).uiModel.photos)
            assertEquals(testImages[0], state.uiModel.photos[0])

            job.cancel()
        }

    @Test
    fun uiState_shouldUpdate_whenRepositoryEmitsNewData() =
        runTest {
            val job = launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

            testLifeLogRepository.sendLogs(listOf(testLifeLogs.first()))

            var state = viewModel.uiState.value
            assertEquals(1, (state as PhotoUIState.Success).uiModel.photos.size)
            assertEquals(testLifeLogs.first().imgs.first(), state.uiModel.photos.first())

            testLifeLogRepository.sendLogs(testLifeLogs)
            state = viewModel.uiState.value
            val testImages = testLifeLogs.flatMap { it.imgs }

            assertEquals(testImages, (state as PhotoUIState.Success).uiModel.photos)

            job.cancel()
        }
}
