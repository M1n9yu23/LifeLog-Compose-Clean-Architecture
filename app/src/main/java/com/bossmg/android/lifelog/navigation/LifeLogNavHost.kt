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
            onBack = navController::popBackStack,
        )
    }
}
