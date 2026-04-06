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

    override fun getLifeLogs(): Flow<List<LifeLogEntity>> = logsFlow

    override fun getLifeLogsByDate(date: String): Flow<List<LifeLogEntity>> =
        logsFlow.map { list -> list.filter { it.date == date } }

    override fun getLifeLogsByMood(mood: String): Flow<List<LifeLogEntity>> =
        logsFlow.map { list -> list.filter { it.mood == mood } }

    override fun getImages(): Flow<List<String>> =
        logsFlow.map { list -> list.mapNotNull { it.img } }

    override suspend fun getLifeLogById(lifeLogId: Int): LifeLogEntity =
        logsFlow.value.first { it.id == lifeLogId }

    override suspend fun insertLifeLog(lifeLogEntity: LifeLogEntity): Long {
        logsFlow.update { current ->
            current + lifeLogEntity
        }
        return lifeLogEntity.id.toLong()
    }

    override suspend fun upsertLifeLog(lifeLogEntity: LifeLogEntity): Long {
        logsFlow.update { current ->
            val index = current.indexOfFirst { it.id == lifeLogEntity.id }
            if (index == -1) {
                current + lifeLogEntity
            } else {
                current.toMutableList().apply {
                    this[index] = lifeLogEntity
                }
            }
        }
        return lifeLogEntity.id.toLong()
    }

    override fun getLifeLogsByMonth(monthPrefix: String): Flow<List<LifeLogEntity>> =
        logsFlow.map { list -> list.filter { it.date.startsWith(monthPrefix) } }

    override suspend fun deleteLifeLogById(lifeLogId: Int) {
        logsFlow.update { current ->
            current.filterNot { it.id == lifeLogId }
        }
    }
}
