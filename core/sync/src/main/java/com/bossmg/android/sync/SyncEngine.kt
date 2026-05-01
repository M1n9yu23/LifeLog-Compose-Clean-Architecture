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
        syncDataSource.upsertAll(logs.map { it.copy(isSynced = true) })
    }

    private suspend fun processPulls(uid: String) {
        val remoteLogs = firestoreDataSource.getAllLogs(uid).getOrThrow()

        val dirtyIds =
            (syncDataSource.getUnsyncedLogs() + syncDataSource.getDeletedUnsyncedLogs())
                .map { it.id }
                .toSet()
        val syncedLocals = syncDataSource.getSyncedLogs().associateBy { it.id }
        val remoteIds = remoteLogs.map { it.id }.toSet()

        val toUpsert =
            remoteLogs
                .filter { it.id !in dirtyIds }
                .map { remote -> remote.toEntity().copy(imgs = syncedLocals[remote.id]?.imgs ?: "") }
        if (toUpsert.isNotEmpty()) syncDataSource.upsertAll(toUpsert)

        val ghostIds = syncedLocals.keys.filter { it !in remoteIds && it !in dirtyIds }
        if (ghostIds.isNotEmpty()) syncDataSource.hardDeleteAll(ghostIds)
    }
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
