package com.bossmg.android.calendar.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.bossmg.android.calendar.Calendar
import kotlinx.serialization.Serializable

@Serializable
object CalendarRoute

fun NavController.navigateToCalendar(navOptions: NavOptions) =
    navigate(route = CalendarRoute, navOptions)

fun NavGraphBuilder.calendarScreen() {
    composable<CalendarRoute> {
        Calendar()
    }
}