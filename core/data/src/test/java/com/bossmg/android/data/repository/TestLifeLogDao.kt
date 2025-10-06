package com.bossmg.android.data.repository

import com.bossmg.android.data.database.LifeLogDao
import com.bossmg.android.data.model.LifeLogEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class TestLifeLogDao : LifeLogDao {

    private val logsFlow = MutableStateFlow<List<LifeLogEntity>>(emptyList())

    override fun getLifeLogs(): Flow<List<LifeLogEntity>> = logsFlow

    override fun getLifeLogsByDate(date: String): Flow<List<LifeLogEntity>> =
        logsFlow.map { list -> list.filter { it.date == date } }

    override fun getLifeLogsByMood(mood: String): Flow<List<LifeLogEntity>> =
        logsFlow.map { list -> list.filter { it.mood == mood } }


    override suspend fun getLifeLogById(lifeLogId: Int): LifeLogEntity =
        logsFlow.value.first { it.id == lifeLogId }

    override suspend fun insertLifeLog(lifeLogEntity: LifeLogEntity) {
        logsFlow.update { current ->
            current + lifeLogEntity
        }
    }

    override suspend fun upsertLifeLog(lifeLogEntity: LifeLogEntity) {
        logsFlow.update { current ->
            val index = current.indexOfFirst { it.id == lifeLogEntity.id }
            if (index == -1) {
                current + lifeLogEntity
            } else {
                current.toMutableList().apply {
                    this[index] = lifeLogEntity
                }
            }
        }
    }

    override suspend fun deleteLifeLogById(lifeLogId: Int) {
        logsFlow.update { current ->
            current.filterNot { it.id == lifeLogId }
        }
    }
}