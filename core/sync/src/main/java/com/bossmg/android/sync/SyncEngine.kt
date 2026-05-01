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
package com.bossmg.android.sync

import com.bossmg.android.common.di.IoDispatcher
import com.bossmg.android.common.safeRunCatching
import com.bossmg.android.data.datasource.SyncDataSource
import com.bossmg.android.data.model.LifeLogEntity
import com.bossmg.android.domain.repository.AuthRepository
import com.bossmg.android.sync.model.LifeLogRemoteDto
import com.bossmg.android.sync.model.toEntity
import com.bossmg.android.sync.remote.FirestoreDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class SyncEngine @Inject constructor(
    private val authRepository: AuthRepository,
    private val syncDataSource: SyncDataSource,
    private val firestoreDataSource: FirestoreDataSource,
    private val syncPreferences: SyncPreferences,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
) {
    suspend fun push(): Result<Unit> =
        withContext(dispatcher) {
            safeRunCatching {
                val uid = authRepository.getCurrentUser().first()?.uid ?: return@safeRunCatching
                processDeletions(uid)
                processUpserts(uid)
            }
        }

    suspend fun sync(): Result<Unit> =
        withContext(dispatcher) {
            safeRunCatching {
                val uid = authRepository.getCurrentUser().first()?.uid ?: return@safeRunCatching
                processDeletions(uid)
                processUpserts(uid)
                processPulls(uid)
            }
        }

    private suspend fun processDeletions(uid: String) {
        val logs = syncDataSource.getDeletedUnsyncedLogs()
        if (logs.isEmpty()) return
        firestoreDataSource.batchDeleteLogs(uid, logs.map { it.id }).getOrThrow()
        syncDataSource.hardDeleteAll(logs.map { it.id })
    }

    private suspend fun processUpserts(uid: String) {
        val logs = syncDataSource.getUnsyncedLogs()
        if (logs.isEmpty()) return
        firestoreDataSource.batchUpsertLogs(uid, logs.map { it.toRemoteDto() }).getOrThrow()
        syncDataSource.markAsSynced(logs.map { it.id })
    }

    private suspend fun processPulls(uid: String) {
        val lastSyncTime = syncPreferences.getLastSyncTime(uid)
        if (lastSyncTime == 0L) fullPull(uid) else incrementalPull(uid, lastSyncTime)
        syncPreferences.setLastSyncTime(uid, System.currentTimeMillis())
    }

    private suspend fun fullPull(uid: String) {
        val remoteLogs = firestoreDataSource.getAllLogs(uid).getOrThrow()
        val dirty = dirtyIds()
        val syncedLocals = syncDataSource.getSyncedLogs().associateBy { it.id }
        val remoteIds = remoteLogs.map { it.id }.toSet()

        remoteLogs
            .filter { it.id !in dirty }
            .map { it.toEntity().copy(imgs = syncedLocals[it.id]?.imgs ?: "") }
            .takeIf { it.isNotEmpty() }
            ?.let { syncDataSource.upsertAll(it) }

        syncedLocals.keys
            .filter { it !in remoteIds && it !in dirty }
            .takeIf { it.isNotEmpty() }
            ?.let { syncDataSource.hardDeleteAll(it) }
    }

    private suspend fun incrementalPull(uid: String, since: Long) {
        val dirty = dirtyIds()
        val syncedLocals = syncDataSource.getSyncedLogs().associateBy { it.id }

        firestoreDataSource.getLogsSince(uid, since).getOrThrow()
            .filter { it.id !in dirty }
            .map { it.toEntity().copy(imgs = syncedLocals[it.id]?.imgs ?: "") }
            .takeIf { it.isNotEmpty() }
            ?.let { syncDataSource.upsertAll(it) }

        firestoreDataSource.getDeletedLogsSince(uid, since).getOrThrow()
            .filter { it !in dirty }
            .takeIf { it.isNotEmpty() }
            ?.let { syncDataSource.hardDeleteAll(it) }
    }

    private suspend fun dirtyIds(): Set<String> =
        (syncDataSource.getUnsyncedLogs() + syncDataSource.getDeletedUnsyncedLogs())
            .map { it.id }
            .toSet()
}

private fun LifeLogEntity.toRemoteDto() =
    LifeLogRemoteDto(
        id = id,
        date = date,
        title = title,
        description = description,
        mood = mood,
        updatedAt = updatedAt,
    )
