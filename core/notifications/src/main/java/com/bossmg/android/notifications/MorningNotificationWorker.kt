package com.bossmg.android.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class MorningNotificationWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        val notificationHelper = NotificationHelper(applicationContext)

        notificationHelper.send(
            "오늘 하루 힘내요!",
            "작은 목표를 달성하는 하루가 되길 응원해요"
        )

        MorningNotificationScheduler(applicationContext).scheduleDailyNotification()

        return Result.success()
    }
}