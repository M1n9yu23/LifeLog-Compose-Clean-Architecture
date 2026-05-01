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
package com.bossmg.android.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import androidx.room.Upsert
import com.bossmg.android.data.model.LifeLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface LifeLogDao {
    @Query("SELECT * FROM lifelogs WHERE isDeleted = 0 ORDER BY date DESC, updatedAt DESC")
    fun getLifeLogs(): Flow<List<LifeLogEntity>>

    @Query(
        """
            SELECT * FROM lifelogs
            WHERE date = :date AND isDeleted = 0 ORDER BY date DESC, updatedAt DESC
            """,
    )
    fun getLifeLogsByDate(date: String): Flow<List<LifeLogEntity>>

    @Query(
        """
            SELECT * FROM lifelogs
            WHERE date LIKE :monthPrefix || '-%' AND isDeleted = 0 ORDER BY date DESC, updatedAt DESC
        """,
    )
    fun getLifeLogsByMonth(monthPrefix: String): Flow<List<LifeLogEntity>>

    @Query(
        """
            SELECT * FROM lifelogs
            WHERE mood = :mood AND isDeleted = 0 ORDER BY date DESC, updatedAt DESC
        """,
    )
    fun getLifeLogsByMood(mood: String): Flow<List<LifeLogEntity>>

    @Query(
        """
            SELECT imgs FROM lifelogs
            WHERE imgs != '' AND isDeleted = 0 ORDER BY date DESC, updatedAt DESC
        """,
    )
    fun getImages(): Flow<List<String>>

    @Query("SELECT * FROM lifelogs WHERE id = :lifeLogId AND isDeleted = 0")
    suspend fun getLifeLogById(lifeLogId: String): LifeLogEntity

    @Insert(onConflict = REPLACE)
    suspend fun insertLifeLog(lifeLogEntity: LifeLogEntity): Long

    @Upsert
    suspend fun upsertLifeLog(lifeLogEntity: LifeLogEntity): Long

    @Query(
        "UPDATE lifelogs SET isDeleted = 1, isSynced = 0, updatedAt = :updatedAt WHERE id = :lifeLogId",
    )
    suspend fun deleteLifeLogById(lifeLogId: String, updatedAt: Long)

    @Query("DELETE FROM lifelogs WHERE id = :lifeLogId")
    suspend fun hardDeleteLifeLogById(lifeLogId: String)

    @Query("DELETE FROM lifelogs WHERE id IN (:ids)")
    suspend fun hardDeleteLifeLogsByIds(ids: List<String>)

    @Query("SELECT * FROM lifelogs WHERE isSynced = 0 AND isDeleted = 0")
    suspend fun getUnsyncedLogs(): List<LifeLogEntity>

    @Query("SELECT * FROM lifelogs WHERE isDeleted = 1 AND isSynced = 0")
    suspend fun getDeletedUnsyncedLogs(): List<LifeLogEntity>

    @Query("SELECT * FROM lifelogs WHERE isSynced = 1 AND isDeleted = 0")
    suspend fun getSyncedLogs(): List<LifeLogEntity>

    @Upsert
    suspend fun upsertAll(logs: List<LifeLogEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllIgnoreConflict(logs: List<LifeLogEntity>)

    @Query("DELETE FROM lifelogs")
    suspend fun clearAll()
}
