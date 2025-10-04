package com.bossmg.android.designsystem.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.bossmg.android.designsystem.ui.theme.DP1
import com.bossmg.android.designsystem.ui.theme.Gray5

@Composable
fun CustomDivider(
    height: Dp = DP1,
    backgroundColor: Color = Gray5
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .background(color = backgroundColor)
    )
}