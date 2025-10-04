package com.bossmg.android.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.bossmg.android.designsystem.ui.components.CustomCard
import com.bossmg.android.designsystem.ui.components.MemoCardItem
import com.bossmg.android.designsystem.ui.theme.Background
import com.bossmg.android.designsystem.ui.theme.DP12
import com.bossmg.android.designsystem.ui.theme.DP8
import java.time.LocalDate

@Composable
fun Home() {
    val uiModels = listOf(
        HomeUIModel(
            id = 1,
            date = LocalDate.of(2025, 10, 1),
            title = "오늘의 아침",
            mood = "행복"
        ),
        HomeUIModel(
            id = 2,
            date = LocalDate.of(2025, 10, 2),
            title = "점심시간",
            mood = "피곤"
        ),
        HomeUIModel(
            id = 3,
            date = LocalDate.of(2025, 10, 3),
            title = "저녁 산책",
            mood = "편안",
            img = "https://picsum.photos/id/237/200/300"
        ),
        HomeUIModel(
            id = 4,
            date = LocalDate.of(2025, 10, 4),
            title = "영화 감상",
            mood = "즐거움",
        ),
        HomeUIModel(
            id = 5,
            date = LocalDate.of(2025, 10, 5),
            title = "카페에서 작업",
            mood = "집중"
        )
    )

    HomeScreen(
        uiModels
    )
}

@Composable
private fun HomeScreen(
    uiModels: List<HomeUIModel>
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .statusBarsPadding()
            .padding(DP12)
    ) {
        items(uiModels, key = {
            it.id
        }) {
            HomeCard(it)
        }
    }
}

@Composable
private fun HomeCard(uiModel: HomeUIModel) {
    CustomCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = DP8),
        backgroundColor = uiModel.cardColor
    ) {
        MemoCardItem(
            date = uiModel.date,
            title = uiModel.title,
            mood = uiModel.mood,
            img = uiModel.img
        )
    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    HomeScreen(
        listOf(
            HomeUIModel(
                id = 1,
                date = LocalDate.of(2025, 10, 1),
                title = "오늘의 아침",
                mood = "행복"
            ),
            HomeUIModel(
                id = 2,
                date = LocalDate.of(2025, 10, 2),
                title = "점심시간",
                mood = "피곤"
            ),
            HomeUIModel(
                id = 3,
                date = LocalDate.of(2025, 10, 3),
                title = "저녁 산책",
                mood = "편안",
                img = "https://picsum.photos/id/237/200/300"
            ),
            HomeUIModel(
                id = 4,
                date = LocalDate.of(2025, 10, 4),
                title = "영화 감상",
                mood = "즐거움",
            ),
            HomeUIModel(
                id = 5,
                date = LocalDate.of(2025, 10, 5),
                title = "카페에서 작업",
                mood = "집중"
            )
        )
    )
}