package com.bossmg.android.lifelog.ui

import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavDestination.Companion.hasRoute
import com.bossmg.android.designsystem.ui.components.LifeLogScaffold
import com.bossmg.android.designsystem.ui.icons.LifeIcons
import com.bossmg.android.designsystem.ui.theme.Primary
import com.bossmg.android.designsystem.ui.theme.Secondary
import com.bossmg.android.lifelog.navigation.LifeLogNavHost

@Composable
fun LifeLogApp(
    appState: LifeLogAppState
) {
    val currentDestination = appState.currentDestination

    val isTopLevelScreen = appState.topLevelDestinations.any {
        currentDestination?.hasRoute(it.route) == true
    }

    LifeLogScaffold(
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
                                    contentDescription = null
                                )
                            },
                            onClick = {
                                appState.navigateToTopLevelDestination(destination)
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Primary,
                                indicatorColor = Color.Transparent
                            )
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
                    contentColor = Secondary
                ) {
                    Icon(
                        imageVector = LifeIcons.Add,
                        contentDescription = "새 메모"
                    )
                }
            }
        }
    ) {
        LifeLogNavHost(appState)
    }
}