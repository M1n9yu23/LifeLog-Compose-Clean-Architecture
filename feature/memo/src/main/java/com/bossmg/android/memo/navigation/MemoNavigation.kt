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
package com.bossmg.android.memo.navigation

import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.bossmg.android.memo.Memo
import com.bossmg.android.memo.MemoViewModel
import kotlinx.serialization.Serializable

@Serializable
data class MemoRoute(val id: String?)

fun NavController.navigateToMemo(memoId: String? = null, navOptions: NavOptions? = null) =
    navigate(MemoRoute(memoId), navOptions)

fun NavGraphBuilder.memoScreen(
    navController: NavController,
    onBack: (String?) -> Unit,
) {
    composable<MemoRoute> { entry ->
        val id = entry.toRoute<MemoRoute>().id

        val cameraResult by entry.savedStateHandle
            .getStateFlow<String?>(MemoViewModel.CAMERA_RESULT_KEY, null)
            .collectAsStateWithLifecycle()

        Memo(
            id = id,
            onBack = onBack,
            onCamera = { navController.navigateToCamera() },
            cameraResult = cameraResult,
            onCameraResultConsumed = {
                entry.savedStateHandle.remove<String>(MemoViewModel.CAMERA_RESULT_KEY)
            },
        )
    }
    cameraScreen(
        onPhotoCaptured = { absolutePath ->
            navController.previousBackStackEntry
                ?.savedStateHandle
                ?.set(MemoViewModel.CAMERA_RESULT_KEY, absolutePath)
            navController.popBackStack()
        },
        onBack = { navController.popBackStack() },
    )
}
