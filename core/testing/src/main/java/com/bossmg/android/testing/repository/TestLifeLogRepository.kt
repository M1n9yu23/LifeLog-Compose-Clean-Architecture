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

    override fun getImages(): Flow<List<String>> =
        logsFlow.map { list -> list.mapNotNull { it.img } }

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

    override suspend fun searchLifeLogs(query: String): List<LifeLog> {
        val logs = logsFlow.replayCache.firstOrNull() ?: emptyList()
        return logs.filter { it.title.contains(query, ignoreCase = true) || it.description.contains(query, ignoreCase = true) }
    }

    fun sendLogs(logs: List<LifeLog>) {
        logsFlow.tryEmit(logs)
    }
}
