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
import com.bossmg.android.sync.model.toEntity
import com.bossmg.android.sync.remote.FirestoreDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class RestoreManager @Inject constructor(
    private val syncDataSource: SyncDataSource,
    private val firestoreDataSource: FirestoreDataSource,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
) {
    suspend fun restoreFromCloud(uid: String): Result<Unit> =
        withContext(dispatcher) {
            safeRunCatching {
                val remoteLogs = firestoreDataSource.getAllLogs(uid).getOrThrow()
                syncDataSource.insertAllIgnoreConflict(remoteLogs.map { it.toEntity() })
            }
        }
}
