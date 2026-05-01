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
package com.bossmg.android.data.repository

import com.bossmg.android.data.database.LifeLogDao
import com.bossmg.android.data.model.LifeLogEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class TestLifeLogDao : LifeLogDao {
    private val logsFlow = MutableStateFlow<List<LifeLogEntity>>(emptyList())

    override fun getLifeLogs(): Flow<List<LifeLogEntity>> =
        logsFlow.map { it.filter { log -> !log.isDeleted } }

    override fun getLifeLogsByDate(date: String): Flow<List<LifeLogEntity>> =
        logsFlow.map { list -> list.filter { it.date == date && !it.isDeleted } }

    override fun getLifeLogsByMonth(monthPrefix: String): Flow<List<LifeLogEntity>> =
        logsFlow.map { list -> list.filter { it.date.startsWith(monthPrefix) && !it.isDeleted } }

    override fun getLifeLogsByMood(mood: String): Flow<List<LifeLogEntity>> =
        logsFlow.map { list -> list.filter { it.mood == mood && !it.isDeleted } }

    override fun getImages(): Flow<List<String>> =
        logsFlow.map { list -> list.filter { it.imgs.isNotEmpty() && !it.isDeleted }.map { it.imgs } }

    override suspend fun getLifeLogById(lifeLogId: String): LifeLogEntity =
        logsFlow.value.first { it.id == lifeLogId && !it.isDeleted }

    override suspend fun insertLifeLog(lifeLogEntity: LifeLogEntity): Long {
        logsFlow.update { current -> current + lifeLogEntity }
        return 0L
    }

    override suspend fun upsertLifeLog(lifeLogEntity: LifeLogEntity): Long {
        logsFlow.update { current ->
            val index = current.indexOfFirst { it.id == lifeLogEntity.id }
            if (index == -1) {
                current + lifeLogEntity
            } else {
                current.toMutableList().apply { this[index] = lifeLogEntity }
            }
        }
        return 0L
    }

    override suspend fun deleteLifeLogById(lifeLogId: String, updatedAt: Long) {
        logsFlow.update { current ->
            current.map {
                if (it.id == lifeLogId) it.copy(isDeleted = true, isSynced = false, updatedAt = updatedAt) else it
            }
        }
    }

    override suspend fun hardDeleteLifeLogById(lifeLogId: String) {
        logsFlow.update { current -> current.filterNot { it.id == lifeLogId } }
    }

    override suspend fun hardDeleteLifeLogsByIds(ids: List<String>) {
        logsFlow.update { current -> current.filterNot { it.id in ids } }
    }

    override suspend fun getUnsyncedLogs(): List<LifeLogEntity> =
        logsFlow.value.filter { !it.isSynced && !it.isDeleted }

    override suspend fun getDeletedUnsyncedLogs(): List<LifeLogEntity> =
        logsFlow.value.filter { it.isDeleted && !it.isSynced }

    override suspend fun getSyncedLogs(): List<LifeLogEntity> =
        logsFlow.value.filter { it.isSynced && !it.isDeleted }

    override suspend fun upsertAll(logs: List<LifeLogEntity>) {
        logs.forEach { upsertLifeLog(it) }
    }

    override suspend fun insertAllIgnoreConflict(logs: List<LifeLogEntity>) {
        val existingIds = logsFlow.value.map { it.id }.toSet()
        logs.filter { it.id !in existingIds }.forEach { insertLifeLog(it) }
    }

    override suspend fun markAsSynced(ids: List<String>) {
        logsFlow.update { current ->
            current.map { if (it.id in ids) it.copy(isSynced = true) else it }
        }
    }

    override suspend fun clearAll() {
        logsFlow.value = emptyList()
    }
}
