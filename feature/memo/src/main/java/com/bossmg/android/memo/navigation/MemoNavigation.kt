package com.bossmg.android.memo.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.bossmg.android.memo.Memo
import kotlinx.serialization.Serializable

@Serializable
data class MemoRoute(val id: Int?)

fun NavController.navigateToMemo(memoId: Int? = null, navOptions: NavOptions? = null) = navigate(MemoRoute(memoId), navOptions)

fun NavGraphBuilder.memoScreen() {
    composable<MemoRoute> {
        val id = it.toRoute<MemoRoute>().id

        Memo(id = id)
    }
}