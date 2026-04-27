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

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.bossmg.android.data.model.LifeLogEntity

@Database(entities = [LifeLogEntity::class], version = 2)
internal abstract class LifeLogDatabase : RoomDatabase() {
    abstract fun lifeLogDao(): LifeLogDao

    companion object {
        val MIGRATION_1_2 =
            object : Migration(1, 2) {
                override fun migrate(db: SupportSQLiteDatabase) {
                    db.execSQL(
                        """
                        CREATE TABLE lifelogs_new (
                            id          INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                            date        TEXT    NOT NULL,
                            title       TEXT    NOT NULL,
                            description TEXT    NOT NULL,
                            mood        TEXT    NOT NULL,
                            imgs        TEXT    NOT NULL DEFAULT ''
                        )
                        """.trimIndent(),
                    )
                    db.execSQL(
                        """
                        INSERT INTO lifelogs_new (id, date, title, description, mood, imgs)
                        SELECT id, date, title, description, mood, COALESCE(img, '') FROM lifelogs
                        """.trimIndent(),
                    )
                    db.execSQL("DROP TABLE lifelogs")
                    db.execSQL("ALTER TABLE lifelogs_new RENAME TO lifelogs")
                }
            }
    }
}
