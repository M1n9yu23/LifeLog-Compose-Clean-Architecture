package com.bossmg.android.data.repository

import com.bossmg.android.domain.model.LifeLog
import com.bossmg.android.domain.repository.LifeLogRepository
import kotlinx.coroutines.flow.Flow

class LifeLogRepositoryImpl : LifeLogRepository {
    override fun getLifeLogs(): Flow<List<LifeLog>> {
        TODO("Not yet implemented")
    }

    override fun getLifeLogsByDate(date: String): Flow<List<LifeLog>> {
        TODO("Not yet implemented")
    }

    override suspend fun getLifeLogById(id: Int): LifeLog {
        TODO("Not yet implemented")
    }

    override suspend fun insertLifeLog(lifeLog: LifeLog) {
        TODO("Not yet implemented")
    }

    override suspend fun upsertLifeLog(lifeLog: LifeLog) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteLifeLogById(id: Int) {
        TODO("Not yet implemented")
    }
}