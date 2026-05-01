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

import com.bossmg.android.data.model.LifeLogEntity

interface SyncDataSource {
    suspend fun getUnsyncedLogs(): List<LifeLogEntity>

    suspend fun getDeletedUnsyncedLogs(): List<LifeLogEntity>

    suspend fun upsertAll(logs: List<LifeLogEntity>)

    suspend fun insertAllIgnoreConflict(logs: List<LifeLogEntity>)

    suspend fun hardDeleteAll(ids: List<String>)

    suspend fun getSyncedLogs(): List<LifeLogEntity>

    suspend fun markAsSynced(ids: List<String>)
}
