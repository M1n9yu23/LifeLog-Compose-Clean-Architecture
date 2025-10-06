package com.bossmg.android.domain.repository

import com.bossmg.android.domain.model.LifeLog
import kotlinx.coroutines.flow.Flow

interface LifeLogRepository {
    fun getLifeLogs(): Flow<List<LifeLog>>
    fun getLifeLogsByDate(date: String): Flow<List<LifeLog>>
    fun getLifeLogsByMood(mood:String): Flow<List<LifeLog>>
    fun getImages(): Flow<List<String>>
    suspend fun getLifeLogById(id: Int): LifeLog
    suspend fun insertLifeLog(lifeLog: LifeLog)
    suspend fun upsertLifeLog(lifeLog: LifeLog)
    suspend fun deleteLifeLogById(id: Int)
}