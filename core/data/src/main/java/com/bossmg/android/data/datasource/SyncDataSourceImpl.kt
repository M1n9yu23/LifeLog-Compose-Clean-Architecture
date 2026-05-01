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
package com.bossmg.android.data.datasource

import com.bossmg.android.data.database.LifeLogDao
import com.bossmg.android.data.model.LifeLogEntity
import com.gyugle.hanfts.SearchEngine
import javax.inject.Inject

internal class SyncDataSourceImpl @Inject constructor(
    private val dao: LifeLogDao,
    private val searchEngine: SearchEngine,
) : SyncDataSource {
    override suspend fun getUnsyncedLogs(): List<LifeLogEntity> = dao.getUnsyncedLogs()

    override suspend fun getDeletedUnsyncedLogs(): List<LifeLogEntity> = dao.getDeletedUnsyncedLogs()

    override suspend fun upsertAll(logs: List<LifeLogEntity>) {
        dao.upsertAll(logs)
        indexLogs(logs)
    }

    override suspend fun insertAllIgnoreConflict(logs: List<LifeLogEntity>) {
        dao.insertAllIgnoreConflict(logs)
        indexLogs(logs)
    }

    override suspend fun hardDeleteAll(ids: List<String>) = dao.hardDeleteLifeLogsByIds(ids)

    override suspend fun getSyncedLogs(): List<LifeLogEntity> = dao.getSyncedLogs()

    private fun indexLogs(logs: List<LifeLogEntity>) {
        logs.forEach { entity ->
            searchEngine.indexDocument(entity.id, entity.title, entity.description)
        }
    }
}
