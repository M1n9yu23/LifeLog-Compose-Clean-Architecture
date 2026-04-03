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

import androidx.compose.ui.graphics.vector.ImageVector
import com.bossmg.android.calendar.navigation.CalendarRoute
import com.bossmg.android.designsystem.ui.icons.LifeIcons
import com.bossmg.android.home.navigation.HomeRoute
import com.bossmg.android.mood.navigation.MoodRoute
import com.bossmg.android.photo.navigation.PhotoRoute
import kotlin.reflect.KClass

enum class TopLevelDestination(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val route: KClass<*>,
    val baseRoute: KClass<*> = route,
) {
    Home(
        selectedIcon = LifeIcons.Home,
        unselectedIcon = LifeIcons.Home,
        route = HomeRoute::class,
    ),
    Calendar(
        selectedIcon = LifeIcons.Calendar,
        unselectedIcon = LifeIcons.Calendar,
        route = CalendarRoute::class,
    ),
    Mood(
        selectedIcon = LifeIcons.Mood,
        unselectedIcon = LifeIcons.Mood,
        route = MoodRoute::class,
    ),
    Photo(
        selectedIcon = LifeIcons.PhotoTab,
        unselectedIcon = LifeIcons.PhotoTab,
        route = PhotoRoute::class,
    ),
}
