package com.bossmg.android.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.bossmg.android.data.model.LifeLog

@Database(entities = [LifeLog::class], version = 1)
internal abstract class LifeLogDatabase : RoomDatabase() {
    abstract fun lifeLogDao(): LifeLogDao
}