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
            """
    )
    fun getLifeLogsByDate(date: String): Flow<List<LifeLogEntity>>

    @Query(
        """
            SELECT * FROM lifelogs
            WHERE mood = :mood ORDER BY date DESC, id DESC
        """
    )
    fun getLifeLogsByMood(mood: String): Flow<List<LifeLogEntity>>

    @Query(
        """
            SELECT img FROM lifelogs
            WHERE img IS NOT NULL ORDER BY date DESC, id DESC 
        """
    )
    fun getImages(): Flow<List<String>>

    @Query("SELECT * FROM lifelogs WHERE id = :lifeLogId")
    suspend fun getLifeLogById(lifeLogId: Int): LifeLogEntity

    @Insert(onConflict = REPLACE)
    suspend fun insertLifeLog(lifeLogEntity: LifeLogEntity)

    @Upsert
    suspend fun upsertLifeLog(lifeLogEntity: LifeLogEntity)

    @Query("DELETE FROM lifelogs WHERE id = :lifeLogId")
    suspend fun deleteLifeLogById(lifeLogId: Int)
}