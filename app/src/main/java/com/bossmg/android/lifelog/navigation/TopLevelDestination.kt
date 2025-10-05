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
        route = HomeRoute::class
    ),
    Calendar(
        selectedIcon = LifeIcons.Calendar,
        unselectedIcon = LifeIcons.Calendar,
        route = CalendarRoute::class
    ),
    Mood(
        selectedIcon = LifeIcons.Mood,
        unselectedIcon = LifeIcons.Mood,
        route = MoodRoute::class
    ),
    Photo(
        selectedIcon = LifeIcons.PhotoTab,
        unselectedIcon = LifeIcons.PhotoTab,
        route = PhotoRoute::class
    )
}