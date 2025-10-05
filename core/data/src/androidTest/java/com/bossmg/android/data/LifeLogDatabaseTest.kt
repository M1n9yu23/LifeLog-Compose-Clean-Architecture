package com.bossmg.android.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.bossmg.android.data.database.LifeLogDao
import com.bossmg.android.data.database.LifeLogDatabase
import com.bossmg.android.data.model.LifeLogEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class LifeLogDatabaseTest {

    private lateinit var db: LifeLogDatabase
    private lateinit var dao: LifeLogDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, LifeLogDatabase::class.java).build()
        dao = db.lifeLogDao()

        initData()
    }

    fun initData() = runTest {
        val count = 5
        for (i in 0 until count) {
            val log = LifeLogEntity(
                date = "2025-10-05",
                title = "제목 $i",
                description = "내용 $i",
                mood = if (i % 2 == 0) "\uD83D\uDE0A 기쁨" else "\uD83D\uDE22 슬픔",
                img = null
            )
            dao.insertLifeLog(log)
        }

        val allLogs = dao.getLifeLogs().first()
        assertEquals(count, allLogs.size)
    }

    @After
    @Throws(IOException::class)
    fun teardown() = db.close()

    @Test
    fun insertLifeLog_addsEntityCorrectly() = runTest {
        val newLog = LifeLogEntity(
            date = "2025-10-06",
            title = "제목 추가",
            description = "내용 추가",
            mood = "\uD83D\uDE34 피곤"
        )
        dao.insertLifeLog(newLog)

        val logs = dao.getLifeLogs().first()
        val insert = logs.find { it.title == "제목 추가" }!!
        assertEquals("제목 추가", insert.title)
        assertEquals("내용 추가", insert.description)
        assertEquals("\uD83D\uDE34 피곤", insert.mood)
    }

    @Test
    fun updateLifeLog_updatesExistingEntity() = runTest {
        val logs = dao.getLifeLogs().first()
        val target = logs.first()

        val update = target.copy(title = "업데이트 제목", description = "업데이트 내용")
        dao.upsertLifeLog(update)

        val updateTarget = dao.getLifeLogById(target.id)
        assertEquals(target.id, updateTarget.id)
        assertEquals("업데이트 제목", updateTarget.title)
        assertEquals("업데이트 내용", updateTarget.description)
    }

    @Test
    fun deleteLifeLogById_removesEntity() = runTest {
        val logs = dao.getLifeLogs().first()
        val target = logs.first()

        dao.deleteLifeLogById(target.id)
        val result = dao.getLifeLogs().first()

        val deletedCount = result.count { it.id == target.id }
        assertEquals(0, deletedCount)

        assertEquals(logs.size - 1, result.size)
    }

    @Test
    fun getLifeLogsByDate() = runTest {
        val result = dao.getLifeLogsByDate("2025-10-05").first()
        val result2 = dao.getLifeLogsByDate("2025-10-10").first()

        assertEquals(5, result.size)
        assertEquals(0, result2.size)
    }

    @Test
    fun getLifeLogsByMood() = runTest {
        val happy = dao.getLifeLogsByMood("\uD83D\uDE0A 기쁨").first()
        val sad = dao.getLifeLogsByMood("\uD83D\uDE22 슬픔").first()

        val happyMoods = happy.map { it.mood }
        val sadMoods = sad.map { it.mood }

        assertEquals(List(happy.size) { "\uD83D\uDE0A 기쁨" }, happyMoods)
        assertEquals(List(sad.size) { "\uD83D\uDE22 슬픔" }, sadMoods)
    }
}