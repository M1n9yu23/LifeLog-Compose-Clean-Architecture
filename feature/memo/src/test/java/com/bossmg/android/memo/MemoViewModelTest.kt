package com.bossmg.android.memo

import com.bossmg.android.domain.usecase.DeleteLifeLogByIdUseCase
import com.bossmg.android.domain.usecase.GetLifeLogByIdUseCase
import com.bossmg.android.domain.usecase.InsertLifeLogUseCase
import com.bossmg.android.domain.usecase.UpsertLifeLogUseCase
import com.bossmg.android.testing.data.lifeLogTestData
import com.bossmg.android.testing.repository.TestLifeLogRepository
import com.bossmg.android.testing.rule.MainDispatcherRule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

class MemoViewModelTest {
    @get:Rule
    val rule = MainDispatcherRule()

    private lateinit var testRepository: TestLifeLogRepository
    private lateinit var mapper: MemoMapper
    private lateinit var getLifeLogByIdUseCase: GetLifeLogByIdUseCase
    private lateinit var insertLifeLogUseCase: InsertLifeLogUseCase
    private lateinit var upsertLifeLogUseCase: UpsertLifeLogUseCase
    private lateinit var deleteLifeLogByIdUseCase: DeleteLifeLogByIdUseCase
    private lateinit var viewModel: MemoViewModel

    private val testLifeLog = lifeLogTestData.first()

    @Before
    fun setUp() {
        testRepository = TestLifeLogRepository()
        getLifeLogByIdUseCase = GetLifeLogByIdUseCase(testRepository)
        insertLifeLogUseCase = InsertLifeLogUseCase(testRepository)
        upsertLifeLogUseCase = UpsertLifeLogUseCase(testRepository)
        deleteLifeLogByIdUseCase = DeleteLifeLogByIdUseCase(testRepository)
        mapper = MemoMapper()
        viewModel = MemoViewModel(
            mapper,
            getLifeLogByIdUseCase,
            insertLifeLogUseCase,
            upsertLifeLogUseCase,
            deleteLifeLogByIdUseCase
        )
    }

    @Test
    fun given_nullId_when_load_then_uiModelIsDefault() = runTest {
        viewModel.load(null)
        assertEquals(MemoUIModel(), viewModel.uiModel.value)
    }

    @Test
    fun given_validId_when_load_then_uiModelReflectsLifeLog() = runTest {
        testRepository.sendLogs(listOf(testLifeLog))

        viewModel.load(testLifeLog.id)

        val state = viewModel.uiModel.value
        assertEquals(testLifeLog.id, state.id)
        assertEquals(testLifeLog.title, state.title)
        assertEquals(testLifeLog.description, state.description)
        assertEquals(LocalDate.parse(testLifeLog.date), state.selectedDate)
        assertEquals(testLifeLog.mood, state.selectedMood)
        assertEquals(testLifeLog.img, state.img)
    }

    @Test
    fun given_invalidId_when_load_then_uiModelIsDefault() = runTest {
        viewModel.load(-1)
        assertEquals(MemoUIModel(), viewModel.uiModel.value)
    }

    @Test
    fun given_title_when_updateTitle_then_uiModelUpdated() {
        viewModel.updateTitle("제목 입력")
        assertEquals("제목 입력", viewModel.uiModel.value.title)
    }

    @Test
    fun given_description_when_updateDescription_then_uiModelUpdated() {
        viewModel.updateDescription("내용 입력")
        assertEquals("내용 입력", viewModel.uiModel.value.description)
    }

    @Test
    fun given_date_when_updateDate_then_uiModelUpdated() {
        val date = LocalDate.of(2025, 10, 7)
        viewModel.updateDate(date)
        assertEquals(date, viewModel.uiModel.value.selectedDate)
    }

    @Test
    fun given_mood_when_updateMood_then_uiModelUpdated() {
        val mood = "\uD83E\uDD29 설렘"
        viewModel.updateMood(mood)
        assertEquals(mood, viewModel.uiModel.value.selectedMood)
    }

    @Test
    fun given_image_when_updateImage_then_uiModelUpdated() {
        val img = "이미지추가.jpg"
        viewModel.updateImage(img)
        assertEquals(img, viewModel.uiModel.value.img)
    }

    @Test
    fun given_uiModel_when_saveMemo_then_repositoryContainsData() = runTest {
        viewModel.updateTitle("제목 입력")
        viewModel.updateDescription("내용 입력")
        viewModel.updateDate(LocalDate.of(2025, 10, 7))
        viewModel.updateMood("\uD83E\uDD29 설렘")
        viewModel.updateImage("이미지추가.jpg")

        viewModel.saveMemo()

        val saveLog = testRepository.getLifeLogs().first().find { it.title == "제목 입력" }
        assertNotNull(saveLog)
        assertEquals("제목 입력", saveLog?.title)
        assertEquals("이미지추가.jpg", saveLog?.img)
        assertEquals("내용 입력", saveLog?.description)
    }

    @Test
    fun given_validId_when_deleteMemo_then_repositoryDataRemoved() = runTest {
        testRepository.sendLogs(listOf(testLifeLog))

        viewModel.deleteMemo(testLifeLog.id)

        viewModel.load(testLifeLog.id)

        assertEquals(MemoUIModel(), viewModel.uiModel.value)
    }
}