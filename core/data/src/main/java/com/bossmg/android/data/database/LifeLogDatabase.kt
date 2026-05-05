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
                            WHEN '📝 메모' THEN '메모'
                            WHEN '😊 기쁨' THEN '기쁨'
                            WHEN '🥰 행복' THEN '행복'
                            WHEN '🤩 설렘' THEN '설렘'
                            WHEN '😍 사랑' THEN '사랑'
                            WHEN '😎 뿌듯함' THEN '뿌듯함'
                            WHEN '😐 무난함' THEN '무난함'
                            WHEN '🤔 고민' THEN '고민'
                            WHEN '😴 피곤' THEN '피곤'
                            WHEN '😢 슬픔' THEN '슬픔'
                            WHEN '😡 화남' THEN '화남'
                            WHEN '😰 불안함' THEN '불안함'
                            WHEN '😞 실망함' THEN '실망함'
                            WHEN '😩 피곤함' THEN '피곤함'
                            ELSE mood
                        END
                        """.trimIndent(),
                    )
                }
            }
    }
}
