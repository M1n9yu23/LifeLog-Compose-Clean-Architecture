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
package com.bossmg.android.lifelog.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavDestination.Companion.hasRoute
import com.bossmg.android.designsystem.ui.components.LifeLogScaffold
import com.bossmg.android.designsystem.ui.icons.LifeIcons
import com.bossmg.android.designsystem.ui.theme.Primary
import com.bossmg.android.designsystem.ui.theme.Secondary
import com.bossmg.android.lifelog.navigation.LifeLogNavHost

@Composable
fun LifeLogApp(
    appState: LifeLogAppState,
) {
    val currentDestination = appState.currentDestination
    val snackbarHostState = remember { SnackbarHostState() }

    val isTopLevelScreen =
        appState.topLevelDestinations.any {
            currentDestination?.hasRoute(it.route) == true
        }

    LifeLogScaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (isTopLevelScreen) {
                NavigationBar(containerColor = Secondary) {
                    appState.topLevelDestinations.forEach { destination ->
                        val selected = appState.currentTopLevelDestination == destination
                        NavigationBarItem(
                            selected = selected,
                            icon = {
                                Icon(
                                    imageVector = if (selected) destination.selectedIcon else destination.unselectedIcon,
                                    contentDescription = null,
                                )
                            },
                            onClick = {
                                appState.navigateToTopLevelDestination(destination)
                            },
                            colors =
                                NavigationBarItemDefaults.colors(
                                    selectedIconColor = Primary,
                                    indicatorColor = Color.Transparent,
                                ),
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            if (isTopLevelScreen) {
                FloatingActionButton(
                    onClick = { appState.navigateToMemo() },
                    containerColor = Primary,
                    contentColor = Secondary,
                ) {
                    Icon(
                        imageVector = LifeIcons.Add,
                        contentDescription = "새 메모",
                    )
                }
            }
        },
    ) {
        LifeLogNavHost(appState, snackbarHostState, Modifier.padding(it))
    }
}
