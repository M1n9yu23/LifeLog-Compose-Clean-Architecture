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
package com.bossmg.android.domain.repository

import com.bossmg.android.domain.model.LifeLog
import kotlinx.coroutines.flow.Flow

interface LifeLogRepository {
    fun getLifeLogs(): Flow<List<LifeLog>>

    fun getLifeLogsByDate(date: String): Flow<List<LifeLog>>

    fun getLifeLogsByMood(mood: String): Flow<List<LifeLog>>

    fun getImages(): Flow<List<String>>

    suspend fun getLifeLogById(id: Int): LifeLog

    suspend fun insertLifeLog(lifeLog: LifeLog)

    suspend fun upsertLifeLog(lifeLog: LifeLog)

    suspend fun deleteLifeLogById(id: Int)

    suspend fun searchLifeLogs(query: String): List<LifeLog>
}
