package com.bossmg.android.data.repository

import com.bossmg.android.data.database.LifeLogDao
import com.bossmg.android.data.mapper.LifeLogMapper
import com.bossmg.android.domain.model.LifeLog
import com.bossmg.android.domain.repository.LifeLogRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LifeLogRepositoryImpl @Inject constructor(
    private val dao: LifeLogDao,
    private val mapper: LifeLogMapper
) : LifeLogRepository {
    override fun getLifeLogs(): Flow<List<LifeLog>> =
        dao.getLifeLogs().map { entities ->
            entities.map { mapper.map(it) }
        }

    override fun getLifeLogsByDate(date: String): Flow<List<LifeLog>> =
        dao.getLifeLogsByDate(date).map { entities ->
            entities.map { mapper.map(it) }
        }

    override fun getLifeLogsByMood(mood: String): Flow<List<LifeLog>> =
        dao.getLifeLogsByMood(mood).map { entities ->
            entities.map { mapper.map(it) }
        }

    override fun getImages(): Flow<List<String>> = dao.getImages()

    override suspend fun getLifeLogById(id: Int): LifeLog = dao.getLifeLogById(id).run {
        mapper.map(this)
    }

    override suspend fun insertLifeLog(lifeLog: LifeLog) {
        dao.insertLifeLog(mapper.mapBack(lifeLog))
    }

    override suspend fun upsertLifeLog(lifeLog: LifeLog) {
        dao.upsertLifeLog(mapper.mapBack(lifeLog))
    }

    override suspend fun deleteLifeLogById(id: Int) {
        dao.deleteLifeLogById(id)
    }
}