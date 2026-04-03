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
import com.bossmg.android.data.di.ApplicationScope
import com.bossmg.android.data.mapper.LifeLogMapper
import com.bossmg.android.domain.model.LifeLog
import com.bossmg.android.domain.repository.LifeLogRepository
import com.bossmg.android.fts.SearchEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LifeLogRepositoryImpl @Inject constructor(
    private val dao: LifeLogDao,
    private val mapper: LifeLogMapper,
    private val searchEngine: SearchEngine,
    @ApplicationScope private val scope: CoroutineScope,
) : LifeLogRepository {
    init {
        scope.launch(Dispatchers.IO) {
            runCatching {
                val entities = dao.getLifeLogs().first()
                val docs = entities.map { Triple(it.id, it.title, it.description) }
                searchEngine.rebuildIndex(docs)
            }
        }
    }

    override fun getLifeLogs(): Flow<List<LifeLog>> =
        dao.getLifeLogs().map { entities -> entities.map { mapper.map(it) } }

    override fun getLifeLogsByDate(date: String): Flow<List<LifeLog>> =
        dao.getLifeLogsByDate(date).map { entities -> entities.map { mapper.map(it) } }

    override fun getLifeLogsByMood(mood: String): Flow<List<LifeLog>> =
        dao.getLifeLogsByMood(mood).map { entities -> entities.map { mapper.map(it) } }

    override fun getImages(): Flow<List<String>> = dao.getImages()

    override suspend fun getLifeLogById(id: Int): LifeLog =
        dao.getLifeLogById(id).run { mapper.map(this) }

    override suspend fun insertLifeLog(lifeLog: LifeLog) {
        val rowId = dao.insertLifeLog(mapper.mapBack(lifeLog))
        withContext(Dispatchers.IO) {
            searchEngine.indexDocument(rowId.toInt(), lifeLog.title, lifeLog.description)
        }
    }

    override suspend fun upsertLifeLog(lifeLog: LifeLog) {
        val rowId = dao.upsertLifeLog(mapper.mapBack(lifeLog))
        val actualId = if (lifeLog.id != 0) lifeLog.id else rowId.toInt()
        withContext(Dispatchers.IO) {
            searchEngine.indexDocument(actualId, lifeLog.title, lifeLog.description)
        }
    }

    override suspend fun deleteLifeLogById(id: Int) {
        dao.deleteLifeLogById(id)
        withContext(Dispatchers.IO) {
            searchEngine.removeDocument(id)
        }
    }

    override suspend fun searchLifeLogs(query: String): List<LifeLog> {
        if (query.isBlank()) return emptyList()
        val ids = withContext(Dispatchers.IO) { searchEngine.search(query) }
        return ids.mapNotNull { id ->
            runCatching { dao.getLifeLogById(id) }.getOrNull()?.let { mapper.map(it) }
        }
    }
}
