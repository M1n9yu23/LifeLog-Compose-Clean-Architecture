package com.bossmg.android.lifelog.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.bossmg.android.calendar.navigation.navigateToCalendar
import com.bossmg.android.home.navigation.navigateToHome
import com.bossmg.android.lifelog.navigation.TopLevelDestination
import com.bossmg.android.memo.navigation.navigateToMemo
import com.bossmg.android.mood.navigation.navigateToMood
import com.bossmg.android.photo.navigation.navigateToPhoto
import kotlinx.coroutines.CoroutineScope

@Composable
fun rememberLifeLogAppState(
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    navController: NavHostController = rememberNavController()
): LifeLogAppState {
    return remember(navController, coroutineScope) {
        LifeLogAppState(
            navController = navController,
            coroutineScope = coroutineScope,
        )
    }
}

@Stable
class LifeLogAppState(
    val navController: NavHostController,
    private val coroutineScope: CoroutineScope
) {
    val topLevelDestinations = TopLevelDestination.entries

    val currentDestination: NavDestination?
        @Composable get() = navController
            .currentBackStackEntryFlow
            .collectAsState(initial = null)
            .value?.destination

    val currentTopLevelDestination: TopLevelDestination?
        @Composable get() = TopLevelDestination.entries
            .firstOrNull { destination ->
                currentDestination?.hasRoute(destination.route) == true
            }

    fun navigateToTopLevelDestination(topLevelDestination: TopLevelDestination) {
        val topLevelNavOptions = navOptions {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }

        when (topLevelDestination) {
            TopLevelDestination.Home -> navController.navigateToHome(topLevelNavOptions)
            TopLevelDestination.Calendar -> navController.navigateToCalendar(topLevelNavOptions)
            TopLevelDestination.Mood -> navController.navigateToMood(topLevelNavOptions)
            TopLevelDestination.Photo -> navController.navigateToPhoto(topLevelNavOptions)
        }
    }

    fun navigateToMemo(memoId: Int? = null) = navController.navigateToMemo(memoId)
}