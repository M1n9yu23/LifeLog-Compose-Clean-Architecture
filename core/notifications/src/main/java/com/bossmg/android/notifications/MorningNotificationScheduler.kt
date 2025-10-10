package com.bossmg.android.notifications

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.Calendar
import java.util.concurrent.TimeUnit

class MorningNotificationScheduler(
    private val context: Context
) {
    fun scheduleDailyNotification() {
        val now = Calendar.getInstance()
        val next8am = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 8)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            if (before(now)) add(Calendar.DAY_OF_YEAR, 1)
        }

        val initialDelay = next8am.timeInMillis - now.timeInMillis

        val workRequest = OneTimeWorkRequestBuilder<MorningNotificationWorker>()
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .addTag("daily_morning_notification")
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "daily_morning_notification",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }
}