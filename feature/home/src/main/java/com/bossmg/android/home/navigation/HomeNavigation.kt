package com.bossmg.android.home.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.bossmg.android.home.Home
import kotlinx.serialization.Serializable

@Serializable
object HomeRoute

fun NavController.navigateToHome(navOptions: NavOptions) = navigate(HomeRoute, navOptions)

fun NavGraphBuilder.homeScreen() {
    composable<HomeRoute> {
        Home()
    }
}