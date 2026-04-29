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
import com.bossmg.android.data.di.IoDispatcher
import com.bossmg.android.data.mapper.LifeLogMapper
import com.bossmg.android.domain.model.LifeLog
import com.bossmg.android.domain.repository.LifeLogReadRepository
import com.bossmg.android.domain.repository.LifeLogSearchRepository
import com.bossmg.android.domain.repository.LifeLogWriteRepository
import com.gyugle.hanfts.SearchEngine
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

internal class LifeLogRepositoryImpl @Inject constructor(
    private val dao: LifeLogDao,
    private val mapper: LifeLogMapper,
    private val searchEngine: SearchEngine,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : LifeLogReadRepository, LifeLogWriteRepository, LifeLogSearchRepository {
    override fun getLifeLogs(): Flow<List<LifeLog>> =
        dao.getLifeLogs().map { entities -> entities.map { mapper.map(it) } }

    override fun getLifeLogsByDate(date: LocalDate): Flow<List<LifeLog>> =
        dao.getLifeLogsByDate(date.toString()).map { entities -> entities.map { mapper.map(it) } }

    override fun getLifeLogsByMonth(yearMonth: YearMonth): Flow<List<LifeLog>> =
        dao.getLifeLogsByMonth(yearMonth.toString()).map { entities -> entities.map { mapper.map(it) } }

    override fun getLifeLogsByMood(mood: String): Flow<List<LifeLog>> =
        dao.getLifeLogsByMood(mood).map { entities -> entities.map { mapper.map(it) } }

    override fun getImages(): Flow<List<String>> =
        dao.getImages().map { rows ->
            rows.flatMap { it.split("|").filter { uri -> uri.isNotEmpty() } }
        }

    override suspend fun getLifeLogById(id: String): LifeLog =
        dao.getLifeLogById(id).run { mapper.map(this) }

    override suspend fun insertLifeLog(lifeLog: LifeLog) {
        val entity = mapper.mapBack(lifeLog)
        val rowId = dao.insertLifeLog(entity)
        withContext(ioDispatcher) {
            searchEngine.indexDocument(rowId, lifeLog.title, lifeLog.description)
        }
    }

    override suspend fun upsertLifeLog(lifeLog: LifeLog) {
        val entity = mapper.mapBack(lifeLog)
        val rowId = dao.upsertLifeLog(entity)
        withContext(ioDispatcher) {
            searchEngine.indexDocument(rowId, lifeLog.title, lifeLog.description)
        }
    }

    override suspend fun deleteLifeLogById(id: String) {
        dao.deleteLifeLogById(id, System.currentTimeMillis())
    }

    override suspend fun clearAllLifeLogs() {
        dao.clearAll()
    }

    override suspend fun searchLifeLogs(query: String): List<LifeLog> {
        if (query.isBlank()) return emptyList()
        val results = withContext(ioDispatcher) { searchEngine.search(query) }
        return results.mapNotNull { result ->
            runCatching { dao.getLifeLogByRowId(result.id) }.getOrNull()?.let { mapper.map(it) }
        }
    }
}
