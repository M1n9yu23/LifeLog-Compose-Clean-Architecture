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

import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.bossmg.android.data.datasource.SyncDataSource
import com.bossmg.android.domain.repository.SyncRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.util.concurrent.TimeUnit
import javax.inject.Inject

internal class SyncManagerImpl @Inject constructor(
    private val workManager: WorkManager,
    private val restoreManager: RestoreManager,
    private val syncDataSource: SyncDataSource,
    private val syncEngine: SyncEngine,
) : SyncRepository {
    override fun schedulePeriodicSync() {
        val request =
            PeriodicWorkRequestBuilder<SyncWorker>(3, TimeUnit.HOURS)
                .setConstraints(Constraints(requiredNetworkType = NetworkType.UNMETERED))
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
                .build()
        workManager.enqueueUniquePeriodicWork(
            PERIODIC_SYNC_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request,
        )
    }

    override fun scheduleImmediateSync() {
        val request =
            OneTimeWorkRequestBuilder<SyncWorker>()
                .setConstraints(Constraints(requiredNetworkType = NetworkType.CONNECTED))
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
                .build()
        workManager.enqueueUniqueWork(
            IMMEDIATE_SYNC_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            request,
        )
    }

    override fun cancelAllSync() {
        workManager.cancelUniqueWork(PERIODIC_SYNC_WORK_NAME)
        workManager.cancelUniqueWork(IMMEDIATE_SYNC_WORK_NAME)
    }

    override suspend fun restoreFromCloud(uid: String): Result<Unit> =
        restoreManager.restoreFromCloud(uid)

    override suspend fun hasUnsyncedData(): Boolean =
        syncDataSource.getUnsyncedLogs().isNotEmpty()

    override suspend fun syncUpload(): Result<Unit> = syncEngine.push()

    override suspend fun syncNow(): Result<Unit> = syncEngine.sync()

    override val isSyncing: Flow<Boolean> =
        combine(
            workManager.getWorkInfosForUniqueWorkFlow(PERIODIC_SYNC_WORK_NAME),
            workManager.getWorkInfosForUniqueWorkFlow(IMMEDIATE_SYNC_WORK_NAME),
        ) { periodic, immediate ->
            (periodic + immediate).any { it.state == WorkInfo.State.RUNNING }
        }

    private companion object {
        const val PERIODIC_SYNC_WORK_NAME = "lifelog_periodic_sync"
        const val IMMEDIATE_SYNC_WORK_NAME = "lifelog_immediate_sync"
    }
}
