package com.bossmg.android.designsystem.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.bossmg.android.designsystem.ui.theme.Background
import com.bossmg.android.designsystem.ui.theme.Primary

@Composable
fun LoadingScreen(
    backgroundColor: Color = Background,
    indicatorColor: Color = Primary,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = indicatorColor
        )
    }
}