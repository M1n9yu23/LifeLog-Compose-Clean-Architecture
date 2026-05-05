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
import com.bossmg.android.domain.util.MoodProvider

@Database(entities = [LifeLogEntity::class], version = 6)
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

        val MIGRATION_2_3 =
            object : Migration(2, 3) {
                override fun migrate(db: SupportSQLiteDatabase) {
                    db.execSQL("ALTER TABLE lifelogs ADD COLUMN updatedAt INTEGER NOT NULL DEFAULT 0")
                    db.execSQL("ALTER TABLE lifelogs ADD COLUMN isSynced  INTEGER NOT NULL DEFAULT 0")
                }
            }

        val MIGRATION_3_4 =
            object : Migration(3, 4) {
                override fun migrate(db: SupportSQLiteDatabase) {
                    db.execSQL("ALTER TABLE lifelogs ADD COLUMN isDeleted INTEGER NOT NULL DEFAULT 0")
                }
            }

        val MIGRATION_4_5 =
            object : Migration(4, 5) {
                override fun migrate(db: SupportSQLiteDatabase) {
                    db.execSQL(
                        """
                        CREATE TABLE lifelogs_new (
                            id          TEXT    PRIMARY KEY NOT NULL,
                            date        TEXT    NOT NULL,
                            title       TEXT    NOT NULL,
                            description TEXT    NOT NULL,
                            mood        TEXT    NOT NULL,
                            imgs        TEXT    NOT NULL DEFAULT '',
                            updatedAt   INTEGER NOT NULL DEFAULT 0,
                            isSynced    INTEGER NOT NULL DEFAULT 0,
                            isDeleted   INTEGER NOT NULL DEFAULT 0
                        )
                        """.trimIndent(),
                    )
                    db.execSQL(
                        """
                        INSERT INTO lifelogs_new
                        SELECT CAST(id AS TEXT), date, title, description, mood, imgs, updatedAt, isSynced, isDeleted
                        FROM lifelogs
                        """.trimIndent(),
                    )
                    db.execSQL("DROP TABLE lifelogs")
                    db.execSQL("ALTER TABLE lifelogs_new RENAME TO lifelogs")
                }
            }

        val MIGRATION_5_6 =
            object : Migration(5, 6) {
                override fun migrate(db: SupportSQLiteDatabase) {
                    db.execSQL(
                        """
                        UPDATE lifelogs SET mood = CASE mood
                            WHEN '📝 메모' THEN '${MoodProvider.Keys.MEMO}'
                            WHEN '😊 기쁨' THEN '${MoodProvider.Keys.JOY}'
                            WHEN '🥰 행복' THEN '${MoodProvider.Keys.HAPPY}'
                            WHEN '🤩 설렘' THEN '${MoodProvider.Keys.EXCITED}'
                            WHEN '😍 사랑' THEN '${MoodProvider.Keys.LOVE}'
                            WHEN '😎 뿌듯함' THEN '${MoodProvider.Keys.PROUD}'
                            WHEN '😐 무난함' THEN '${MoodProvider.Keys.OKAY}'
                            WHEN '🤔 고민' THEN '${MoodProvider.Keys.WORRIED}'
                            WHEN '😴 피곤' THEN '${MoodProvider.Keys.TIRED}'
                            WHEN '😢 슬픔' THEN '${MoodProvider.Keys.SAD}'
                            WHEN '😡 화남' THEN '${MoodProvider.Keys.ANGRY}'
                            WHEN '😰 불안함' THEN '${MoodProvider.Keys.ANXIOUS}'
                            WHEN '😞 실망함' THEN '${MoodProvider.Keys.DISAPPOINTED}'
                            WHEN '😩 피곤함' THEN '${MoodProvider.Keys.EXHAUSTED}'
                            ELSE mood
                        END
                        """.trimIndent(),
                    )
                }
            }
    }
}
