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

import android.Manifest
import android.R.drawable.ic_notification_overlay
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context,
) : NotificationSender {
    companion object {
        private const val TAG = "NotificationHelper"
    }

    private val channelId = "morning_alarm_channel"

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val name = context.getString(R.string.notification_channel_name)
        val description = context.getString(R.string.notification_channel_description)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel =
            NotificationChannel(channelId, name, importance).apply {
                this.description = description
            }
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    override fun send(title: String, message: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS,
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.d(TAG, "Notification permission not granted")
                return
            }
        }

        val notification =
            NotificationCompat.Builder(context, channelId)
                .setSmallIcon(ic_notification_overlay)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .build()

        try {
            NotificationManagerCompat.from(context)
                .notify(System.currentTimeMillis().toInt(), notification)
        } catch (e: SecurityException) {
            Log.e(TAG, "Permission denied for notification", e)
        }
    }
}
