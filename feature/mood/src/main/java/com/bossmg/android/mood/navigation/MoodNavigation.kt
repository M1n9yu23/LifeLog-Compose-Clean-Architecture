package com.bossmg.android.mood.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.bossmg.android.mood.Mood
import kotlinx.serialization.Serializable

@Serializable
object MoodRoute

fun NavController.navigateToMood(navOptions: NavOptions) = navigate(MoodRoute, navOptions)

fun NavGraphBuilder.moodScreen(
    onMemoItemClick: (Int) -> Unit
) {
    composable<MoodRoute> {
        Mood(onMemoItemClick)
    }
}