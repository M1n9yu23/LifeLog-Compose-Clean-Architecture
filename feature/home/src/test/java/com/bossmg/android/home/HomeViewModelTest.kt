package com.bossmg.android.home

import com.bossmg.android.domain.model.LifeLog
import com.bossmg.android.domain.usecase.GetLifeLogsUseCase
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
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val rule = MainDispatcherRule()

    private lateinit var testLifeLogRepository: TestLifeLogRepository
    private lateinit var getLifeLogsUseCase: GetLifeLogsUseCase
    private lateinit var mapper: HomeMapper
    private lateinit var viewModel: HomeViewModel

    private val testLifeLogs: List<LifeLog> = lifeLogTestData

    @Before
    fun setUp() {
        testLifeLogRepository = TestLifeLogRepository()
        getLifeLogsUseCase = GetLifeLogsUseCase(testLifeLogRepository)
        mapper = HomeMapper()
        viewModel = HomeViewModel(getLifeLogsUseCase, mapper)
    }


    @Test
    fun uiState_shouldLoadingInit() {
        assertEquals(HomeUIState.Loading, viewModel.uiState.value)
    }

    @Test
    fun uiState_shouldEmitSuccess_whenRepositoryHasData() = runTest {
        val job = launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        testLifeLogRepository.sendLogs(testLifeLogs)

        val state = viewModel.uiState.value
        assertTrue(state is HomeUIState.Success)
        assertEquals(testLifeLogs.size, (state as HomeUIState.Success).uiModels.size)
        assertEquals(testLifeLogs[0].title, state.uiModels[0].title)

        job.cancel()
    }

    @Test
    fun uiState_shouldUpdate_whenRepositoryEmitsNewData() = runTest {
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
    fun uiState_shouldMapLifeLogToHomeUIModelCorrectly() = runTest {
        val job = launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        testLifeLogRepository.sendLogs(testLifeLogs)
        val state = viewModel.uiState.value
        assertTrue(state is HomeUIState.Success)
        val successState = state as HomeUIState.Success

        successState.uiModels.forEachIndexed { index, uiModel ->
            val lifeLog = testLifeLogs[index]
            assertEquals(lifeLog.id, uiModel.id)
            assertEquals(LocalDate.parse(lifeLog.date), uiModel.date)
            assertEquals(lifeLog.title, uiModel.title)
            assertEquals(lifeLog.mood, uiModel.mood)
            assertEquals(lifeLog.img, uiModel.img)
        }

        job.cancel()
    }
}