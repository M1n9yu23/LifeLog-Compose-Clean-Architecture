package com.bossmg.android.calendar

import com.bossmg.android.domain.usecase.GetLifeLogsByDateUseCase
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
class CalendarViewModelTest {

    @get:Rule
    val rule = MainDispatcherRule()

    private lateinit var testLifeLogRepository: TestLifeLogRepository
    private lateinit var getLifeLogsByDateUseCase: GetLifeLogsByDateUseCase
    private lateinit var mapper: CalendarMapper
    private lateinit var viewModel: CalendarViewModel

    private val testLifeLogs = lifeLogTestData

    @Before
    fun setUp() {
        testLifeLogRepository = TestLifeLogRepository()
        getLifeLogsByDateUseCase = GetLifeLogsByDateUseCase(testLifeLogRepository)
        mapper = CalendarMapper()
        viewModel = CalendarViewModel(getLifeLogsByDateUseCase, mapper)
    }

    @Test
    fun uiState_shouldLoadingInit() {
        assertEquals(CalendarUIState.Loading, viewModel.uiState.value)
    }

    @Test
    fun uiState_shouldEmitSuccess_whenRepositoryHasData() = runTest {
        val job = launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        testLifeLogRepository.sendLogs(testLifeLogs)

        val state = viewModel.uiState.value
        assertTrue(state is CalendarUIState.Success)
        val lifeLogs = testLifeLogs.filter { it.date == viewModel.selectedDate.value.toString() }
        assertEquals(lifeLogs.size, (state as CalendarUIState.Success).uiModel.memoItems.size)
        assertEquals(lifeLogs[0].title, state.uiModel.memoItems[0].title)

        job.cancel()
    }

    @Test
    fun uiState_shouldUpdate_whenSelectedDateChanges() = runTest {
        val job = launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        val newDate = LocalDate.of(2025, 10, 7)
        viewModel.selectDate(newDate)

        testLifeLogRepository.sendLogs(testLifeLogs.filter { it.date == newDate.toString() })
        val state = viewModel.uiState.value
        assertEquals(1, (state as CalendarUIState.Success).uiModel.memoItems.size)
        assertEquals(newDate, state.uiModel.memoItems[0].date)

        job.cancel()
    }

    @Test
    fun selectedDate_shouldUpdateCorrectly() {
        val newDate = LocalDate.of(2025, 10, 12)
        viewModel.selectDate(newDate)

        assertEquals(newDate, viewModel.selectedDate.value)
    }

    @Test
    fun currentMonth_shouldChangeOnPrevAndNextMonth() {
        val initMonth = viewModel.currentMonth.value
        viewModel.onPrevMonth()
        assertEquals(initMonth.minusMonths(1), viewModel.currentMonth.value)

        viewModel.onNextMonth()
        viewModel.onNextMonth()
        assertEquals(initMonth.plusMonths(1), viewModel.currentMonth.value)
    }

    @Test
    fun uiState_shouldMapLifeLogToCalendarUIModelCorrectly() = runTest {
        val job = launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

        testLifeLogRepository.sendLogs(testLifeLogs)
        val state = viewModel.uiState.value
        assertTrue(state is CalendarUIState.Success)

        val successState = state as CalendarUIState.Success
        val lifeLogs = testLifeLogs.filter { it.date == viewModel.selectedDate.value.toString() }

        successState.uiModel.memoItems.forEachIndexed { index, item ->
            val lifeLog = lifeLogs[index]
            assertEquals(lifeLog.id, item.id)
            assertEquals(LocalDate.parse(lifeLog.date), item.date)
            assertEquals(lifeLog.title, item.title)
            assertEquals(lifeLog.mood, item.mood)
            assertEquals(lifeLog.img, item.img)
        }

        job.cancel()
    }
}