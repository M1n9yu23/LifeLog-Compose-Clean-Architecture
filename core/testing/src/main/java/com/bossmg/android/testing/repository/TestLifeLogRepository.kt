package com.bossmg.android.testing.repository

import com.bossmg.android.domain.model.LifeLog
import com.bossmg.android.domain.repository.LifeLogRepository
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map

class TestLifeLogRepository : LifeLogRepository {

    private val logsFlow: MutableSharedFlow<List<LifeLog>> =
        MutableSharedFlow(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    override fun getLifeLogs(): Flow<List<LifeLog>> = logsFlow

    override fun getLifeLogsByDate(date: String): Flow<List<LifeLog>> =
        logsFlow.map { list -> list.filter { it.date == date } }

    override fun getLifeLogsByMood(mood: String): Flow<List<LifeLog>> =
        logsFlow.map { list -> list.filter { it.mood == mood } }

    override suspend fun getLifeLogById(id: Int): LifeLog {
        val logs = logsFlow.replayCache.firstOrNull() ?: emptyList()
        return logs.first { it.id == id }
    }

    override suspend fun insertLifeLog(lifeLog: LifeLog) {
        val current = logsFlow.replayCache.firstOrNull()?.toMutableList() ?: mutableListOf()
        current.add(lifeLog)
        logsFlow.tryEmit(current)
    }

    override suspend fun upsertLifeLog(lifeLog: LifeLog) {
        val current = logsFlow.replayCache.firstOrNull()?.toMutableList() ?: mutableListOf()
        val index = current.indexOfFirst { it.id == lifeLog.id }
        if (index != -1) current[index] = lifeLog else current.add(lifeLog)
        logsFlow.tryEmit(current)
    }

    override suspend fun deleteLifeLogById(id: Int) {
        val current = logsFlow.replayCache.firstOrNull()?.toMutableList() ?: mutableListOf()
        current.removeIf { it.id == id }
        logsFlow.tryEmit(current)
    }

    fun sendLogs(logs: List<LifeLog>) {
        logsFlow.tryEmit(logs)
    }
}