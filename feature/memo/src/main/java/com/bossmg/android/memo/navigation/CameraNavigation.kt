package com.bossmg.android.memo.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.bossmg.android.memo.camera.CameraScreen
import kotlinx.serialization.Serializable

@Serializable
internal object CameraRoute

internal fun NavController.navigateToCamera(navOptions: NavOptions? = null) =
    navigate(CameraRoute, navOptions)

internal fun NavGraphBuilder.cameraScreen(
    onPhotoCaptured: (String) -> Unit,
    onBack: () -> Unit,
) {
    composable<CameraRoute> {
        CameraScreen(
            onPhotoCaptured = onPhotoCaptured,
            onBack = onBack,
        )
    }
}
