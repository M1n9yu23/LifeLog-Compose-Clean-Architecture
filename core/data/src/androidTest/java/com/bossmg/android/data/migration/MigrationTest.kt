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
package com.bossmg.android.data.migration

import androidx.room.testing.MigrationTestHelper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.bossmg.android.data.database.LifeLogDatabase
import com.bossmg.android.domain.util.MoodProvider
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MigrationTest {
    private val testDb = "migration-test"

    @get:Rule
    val helper =
        MigrationTestHelper(
            InstrumentationRegistry.getInstrumentation(),
            LifeLogDatabase::class.java,
        )

    @Test
    fun migrate5To6_stripsEmojiPrefix() {
        val joyLegacy = "😊 기쁨"
        val sadLegacy = "😢 슬픔"
        val tiredLegacy = "😴 피곤"

        helper.createDatabase(testDb, 5).use { db ->
            db.execSQL(
                "INSERT INTO lifelogs (id, date, title, description, mood, imgs, updatedAt, isSynced, isDeleted) " +
                    "VALUES ('1', '2025-01-01', 'T', '', '$joyLegacy', '', 0, 0, 0)",
            )
            db.execSQL(
                "INSERT INTO lifelogs (id, date, title, description, mood, imgs, updatedAt, isSynced, isDeleted) " +
                    "VALUES ('2', '2025-01-02', 'T', '', '$sadLegacy', '', 0, 0, 0)",
            )
            db.execSQL(
                "INSERT INTO lifelogs (id, date, title, description, mood, imgs, updatedAt, isSynced, isDeleted) " +
                    "VALUES ('3', '2025-01-03', 'T', '', '$tiredLegacy', '', 0, 0, 0)",
            )
        }

        helper.runMigrationsAndValidate(testDb, 6, true, LifeLogDatabase.MIGRATION_5_6).use { db ->
            fun queryMood(id: String): String {
                val cursor = db.query("SELECT mood FROM lifelogs WHERE id = ?", arrayOf(id))
                cursor.moveToFirst()
                return cursor.getString(0).also { cursor.close() }
            }

            assertEquals(MoodProvider.Keys.JOY, queryMood("1"))
            assertEquals(MoodProvider.Keys.SAD, queryMood("2"))
            assertEquals(MoodProvider.Keys.TIRED, queryMood("3"))
        }
    }

    @Test
    fun migrate5To6_preservesAlreadyCleanValues() {
        helper.createDatabase(testDb, 5).use { db ->
            db.execSQL(
                "INSERT INTO lifelogs (id, date, title, description, mood, imgs, updatedAt, isSynced, isDeleted) " +
                    "VALUES ('10', '2025-01-01', 'T', '', '${MoodProvider.Keys.JOY}', '', 0, 0, 0)",
            )
        }

        helper.runMigrationsAndValidate(testDb, 6, true, LifeLogDatabase.MIGRATION_5_6).use { db ->
            val cursor = db.query("SELECT mood FROM lifelogs WHERE id = ?", arrayOf("10"))
            cursor.moveToFirst()
            assertEquals(MoodProvider.Keys.JOY, cursor.getString(0))
            cursor.close()
        }
    }
}
