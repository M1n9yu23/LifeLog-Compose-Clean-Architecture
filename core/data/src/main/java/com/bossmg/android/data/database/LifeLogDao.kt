package com.bossmg.android.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import androidx.room.Upsert
import com.bossmg.android.data.model.LifeLog
import kotlinx.coroutines.flow.Flow

@Dao
interface LifeLogDao {
    @Query("SELECT * FROM lifelogs ORDER BY date DESC, id DESC")
    fun getLifeLogs(): Flow<List<LifeLog>>

    @Query(
        """
            SELECT * FROM lifelogs
            WHERE date = :date ORDER BY date DESC, id DESC
            """
    )
    fun getLifeLogsByDate(date: String): Flow<List<LifeLog>>

    @Query("SELECT * FROM lifelogs WHERE id = :lifeLogId")
    suspend fun getLifeLogById(lifeLogId: Int): LifeLog

    @Insert(onConflict = REPLACE)
    suspend fun insertLifeLog(lifeLog: LifeLog)

    @Upsert
    suspend fun upsertLifeLog(lifeLog: LifeLog)

    @Query("DELETE FROM lifelogs WHERE id = :lifeLogId")
    suspend fun deleteLifeLogById(lifeLogId: Int)
}