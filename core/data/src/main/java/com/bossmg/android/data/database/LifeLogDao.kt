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
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import androidx.room.Upsert
import com.bossmg.android.data.model.LifeLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LifeLogDao {
    @Query("SELECT * FROM lifelogs ORDER BY date DESC, id DESC")
    fun getLifeLogs(): Flow<List<LifeLogEntity>>

    @Query(
        """
            SELECT * FROM lifelogs
            WHERE date = :date ORDER BY date DESC, id DESC
            """,
    )
    fun getLifeLogsByDate(date: String): Flow<List<LifeLogEntity>>

    @Query(
        """
            SELECT * FROM lifelogs
            WHERE mood = :mood ORDER BY date DESC, id DESC
        """,
    )
    fun getLifeLogsByMood(mood: String): Flow<List<LifeLogEntity>>

    @Query(
        """
            SELECT img FROM lifelogs
            WHERE img IS NOT NULL ORDER BY date DESC, id DESC 
        """,
    )
    fun getImages(): Flow<List<String>>

    @Query("SELECT * FROM lifelogs WHERE id = :lifeLogId")
    suspend fun getLifeLogById(lifeLogId: Int): LifeLogEntity

    @Insert(onConflict = REPLACE)
    suspend fun insertLifeLog(lifeLogEntity: LifeLogEntity): Long

    @Upsert
    suspend fun upsertLifeLog(lifeLogEntity: LifeLogEntity): Long

    @Query("DELETE FROM lifelogs WHERE id = :lifeLogId")
    suspend fun deleteLifeLogById(lifeLogId: Int)
}
