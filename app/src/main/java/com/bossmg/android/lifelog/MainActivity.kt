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
package com.bossmg.android.lifelog

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bossmg.android.designsystem.ui.theme.LifeLogTheme
import com.bossmg.android.domain.enums.ThemeConfig
import com.bossmg.android.domain.repository.ThemeRepository
import com.bossmg.android.lifelog.ui.LifeLogApp
import com.bossmg.android.lifelog.ui.rememberLifeLogAppState
import com.bossmg.android.notifications.MorningNotificationScheduler
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var notificationScheduler: MorningNotificationScheduler

    @Inject
    lateinit var themeRepository: ThemeRepository

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission(),
        ) {
            if (it) {
                scheduleNotifications()
            } else {
                showPermissionRationale()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkNotificationPermission()

        enableEdgeToEdge()
        setContent {
            val themeConfig by themeRepository.getThemeConfig()
                .collectAsStateWithLifecycle(ThemeConfig.FOLLOW_SYSTEM)
            val darkTheme =
                when (themeConfig) {
                    ThemeConfig.FOLLOW_SYSTEM -> isSystemInDarkTheme()
                    ThemeConfig.LIGHT -> false
                    ThemeConfig.DARK -> true
                }

            LifeLogTheme(darkTheme = darkTheme) {
                val appState = rememberLifeLogAppState()
                LifeLogApp(appState)
            }
        }
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS,
                ) == PackageManager.PERMISSION_GRANTED -> {
                    scheduleNotifications()
                }

                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    showPermissionRationale()
                }

                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            scheduleNotifications()
        }
    }

    private fun showPermissionRationale() {
        AlertDialog.Builder(this)
            .setTitle("알림 권한 필요")
            .setMessage("앱에서 알림을 보내려면 알림 권한이 필요해요. 설정에서 허용해주세요.")
            .setPositiveButton("설정") { _, _ ->
                openAppSettings()
            }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun openAppSettings() {
        val intent =
            Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", packageName, null),
            )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun scheduleNotifications() {
        notificationScheduler.scheduleDailyNotification()
    }
}
