package com.bossmg.android.photo.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.bossmg.android.photo.Photo
import kotlinx.serialization.Serializable

@Serializable
object PhotoRoute

fun NavController.navigateToPhoto(navOptions: NavOptions) = navigate(PhotoRoute, navOptions)

fun NavGraphBuilder.photoScreen() {
    composable<PhotoRoute> {
        Photo()
    }
}