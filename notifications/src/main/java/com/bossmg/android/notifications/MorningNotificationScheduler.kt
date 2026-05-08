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
package com.bossmg.android.notifications

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MorningNotificationScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    fun scheduleDailyNotification() {
        val now = Calendar.getInstance()
        val next8am =
            Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 8)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                if (before(now)) add(Calendar.DAY_OF_YEAR, 1)
            }

        val initialDelay = next8am.timeInMillis - now.timeInMillis

        val workRequest =
            OneTimeWorkRequestBuilder<MorningNotificationWorker>()
                .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                .addTag("daily_morning_notification")
                .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "daily_morning_notification",
            ExistingWorkPolicy.REPLACE,
            workRequest,
        )
    }
}
