package com.bossmg.android.photo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.bossmg.android.designsystem.ui.theme.Background
import com.bossmg.android.designsystem.ui.theme.Black
import com.bossmg.android.designsystem.ui.theme.DP12
import com.bossmg.android.designsystem.ui.theme.DP8

@Composable
fun Photo() {
    val dummyPhotos = listOf(
        "https://picsum.photos/300/300?1",
        "https://picsum.photos/300/300?2",
        "https://picsum.photos/300/300?3",
        "https://picsum.photos/300/300?4",
        "https://picsum.photos/300/300?5",
        "https://picsum.photos/300/300?6",
    )

    PhotoScreen(dummyPhotos)
}

@Composable
private fun PhotoScreen(
    photos: List<String>
) {
    LazyVerticalGrid(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Background)
            .padding(DP12),
        columns = GridCells.Fixed(3),
        verticalArrangement = Arrangement.spacedBy(DP8),
        horizontalArrangement = Arrangement.spacedBy(DP8)
    ) {
        items(photos.size) {
//            AsyncImage(
//                model = it,
//                contentDescription = "사용한 이미지",
//                modifier = Modifier.aspectRatio(1f),
//                alignment = Alignment.Center
//            )
            Box(
                modifier = Modifier
                    .aspectRatio(1f)
                    .background(color = Black),
                contentAlignment = Alignment.Center
            ) {

            }
        }
    }
}

@Preview
@Composable
private fun PhotoScreenPreview() {
    PhotoScreen(
        listOf(
            "https://picsum.photos/300/300?1",
            "https://picsum.photos/300/300?2",
            "https://picsum.photos/300/300?3",
            "https://picsum.photos/300/300?4",
            "https://picsum.photos/300/300?5",
            "https://picsum.photos/300/300?6",
        )
    )
}