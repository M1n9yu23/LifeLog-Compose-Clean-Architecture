package com.bossmg.android.lifelog.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import com.bossmg.android.calendar.navigation.calendarScreen
import com.bossmg.android.home.navigation.HomeRoute
import com.bossmg.android.home.navigation.homeScreen
import com.bossmg.android.lifelog.ui.LifeLogAppState
import com.bossmg.android.memo.navigation.memoScreen
import com.bossmg.android.memo.navigation.navigateToMemo
import com.bossmg.android.mood.navigation.moodScreen
import com.bossmg.android.photo.navigation.photoScreen

@Composable
fun LifeLogNavHost(
    appState: LifeLogAppState,
    modifier: Modifier = Modifier,
) {
    val navController = appState.navController

    NavHost(
        navController = navController,
        startDestination = HomeRoute,
        modifier = modifier,
    ) {
        homeScreen(navController::navigateToMemo)
        calendarScreen(navController::navigateToMemo)
        moodScreen(navController::navigateToMemo)
        photoScreen()
        memoScreen(
            onBack = navController::popBackStack
        )
    }
}