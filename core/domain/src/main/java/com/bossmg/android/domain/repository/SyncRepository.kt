package com.bossmg.android.domain.repository

import kotlinx.coroutines.flow.Flow

interface SyncRepository {
    fun schedulePeriodicSync()
    fun scheduleImmediateSync()
    fun cancelAllSync()
    suspend fun restoreFromCloud(uid: String): Result<Unit>
    suspend fun hasUnsyncedData(): Boolean
    suspend fun syncUpload(): Result<Unit>
    suspend fun syncNow(): Result<Unit>
    val isSyncing: Flow<Boolean>
}
