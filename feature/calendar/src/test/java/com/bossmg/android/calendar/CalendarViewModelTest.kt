package com.bossmg.android.calendar

import com.bossmg.android.domain.usecase.GetLifeLogsByDateUseCase
import com.bossmg.android.domain.usecase.GetLifeLogsByMonthUseCase
import com.bossmg.android.testing.data.lifeLogTestData
import com.bossmg.android.testing.repository.TestLifeLogRepository
import com.bossmg.android.testing.rule.AndroidLogRule
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

    @get:Rule
    val logRule = AndroidLogRule()

    private lateinit var testLifeLogRepository: TestLifeLogRepository
    private lateinit var getLifeLogsByDateUseCase: GetLifeLogsByDateUseCase
    private lateinit var getLifeLogsByMonthUseCase: GetLifeLogsByMonthUseCase
    private lateinit var mapper: CalendarMapper
    private lateinit var viewModel: CalendarViewModel

    private val testLifeLogs = lifeLogTestData

    @Before
    fun setUp() {
        testLifeLogRepository = TestLifeLogRepository()
        getLifeLogsByDateUseCase = GetLifeLogsByDateUseCase(testLifeLogRepository)
        getLifeLogsByMonthUseCase = GetLifeLogsByMonthUseCase(testLifeLogRepository)
        mapper = CalendarMapper()
        viewModel = CalendarViewModel(getLifeLogsByDateUseCase, getLifeLogsByMonthUseCase, mapper)
    }

    @Test
    fun uiState_shouldLoadingInit() {
        assertEquals(CalendarUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun uiState_shouldEmitSuccess_whenRepositoryHasData() =
        runTest {
            val job = launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

            testLifeLogRepository.sendLogs(testLifeLogs)

            val state = viewModel.uiState.value
            assertTrue(state is CalendarUiState.Success)

            val successState = state as CalendarUiState.Success

            val lifeLogs = testLifeLogs.filter { it.date == successState.selectedDate.toString() }

            assertEquals(lifeLogs.size, successState.memoItems.size)
            if (lifeLogs.isNotEmpty()) {
                assertEquals(lifeLogs[0].title, successState.memoItems[0].title)
            }

            job.cancel()
        }

    @Test
    fun uiState_shouldUpdate_whenSelectedDateChanges() =
        runTest {
            val job = launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

            testLifeLogRepository.sendLogs(testLifeLogs)

            val newDate = LocalDate.of(2025, 10, 7)
            viewModel.onDateSelect(newDate)

            testLifeLogRepository.sendLogs(testLifeLogs.filter { it.date == newDate.toString() })

            val state = viewModel.uiState.value
            assertTrue(state is CalendarUiState.Success)

            val successState = state as CalendarUiState.Success

            val expectedLogs = testLifeLogs.filter { it.date == newDate.toString() }
            assertEquals(expectedLogs.size, successState.memoItems.size)
            assertEquals(newDate, successState.memoItems[0].date)

            job.cancel()
        }

    @Test
    fun selectedDate_shouldUpdateCorrectly() =
        runTest {
            val job = launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }
            testLifeLogRepository.sendLogs(testLifeLogs)

            val newDate = LocalDate.of(2025, 10, 12)
            viewModel.onDateSelect(newDate)

            val state = viewModel.uiState.value as CalendarUiState.Success
            assertEquals(newDate, state.selectedDate)

            job.cancel()
        }

    @Test
    fun currentMonth_shouldChangeOnPrevAndNextMonth() =
        runTest {
            val job = launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }
            testLifeLogRepository.sendLogs(testLifeLogs)

            val stateBefore = viewModel.uiState.value as CalendarUiState.Success
            val initMonth = stateBefore.currentMonth

            viewModel.onClickPrevMonth()
            val stateAfterPrev = viewModel.uiState.value as CalendarUiState.Success
            assertEquals(initMonth.minusMonths(1), stateAfterPrev.currentMonth)

            viewModel.onClickNextMonth()
            viewModel.onClickNextMonth()
            val stateAfterNext = viewModel.uiState.value as CalendarUiState.Success
            assertEquals(initMonth.plusMonths(1), stateAfterNext.currentMonth)

            job.cancel()
        }

    @Test
    fun uiState_shouldMapLifeLogToCalendarUIModelCorrectly() =
        runTest {
            val job = launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

            testLifeLogRepository.sendLogs(testLifeLogs)
            val state = viewModel.uiState.value
            assertTrue(state is CalendarUiState.Success)

            val successState = state as CalendarUiState.Success
            val lifeLogs = testLifeLogs.filter { it.date == successState.selectedDate.toString() }

            successState.memoItems.forEachIndexed { index, item ->
                val lifeLog = lifeLogs[index]
                assertEquals(lifeLog.id, item.id)
                assertEquals(LocalDate.parse(lifeLog.date), item.date)
                assertEquals(lifeLog.title, item.title)
                assertEquals(lifeLog.mood, item.mood)
                assertEquals(lifeLog.img, item.img)
            }

            job.cancel()
        }

    @Test
    fun markedDate_shouldEmitCorrectDates_whenRepositoryHasData() =
        runTest {
            val job = launch(UnconfinedTestDispatcher()) { viewModel.uiState.collect() }

            testLifeLogRepository.sendLogs(testLifeLogs)

            val state = viewModel.uiState.value
            assertTrue(state is CalendarUiState.Success)
            val successState = state as CalendarUiState.Success

            val currentMonth = successState.currentMonth

            val monthLogs =
                testLifeLogs.filter {
                    val date = LocalDate.parse(it.date)
                    date.year == currentMonth.year && date.monthValue == currentMonth.monthValue
                }

            val result = monthLogs.map { LocalDate.parse(it.date) }.toSet()

            assertEquals(result, successState.markedDates.toSet())

            job.cancel()
        }
}
