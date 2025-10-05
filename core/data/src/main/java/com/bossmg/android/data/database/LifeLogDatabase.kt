package com.bossmg.android.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.bossmg.android.data.model.LifeLogEntity

@Database(entities = [LifeLogEntity::class], version = 1)
internal abstract class LifeLogDatabase : RoomDatabase() {
    abstract fun lifeLogDao(): LifeLogDao
}