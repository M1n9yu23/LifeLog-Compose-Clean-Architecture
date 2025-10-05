package com.bossmg.android.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bossmg.android.designsystem.ui.components.CustomCard
import com.bossmg.android.designsystem.ui.components.LoadingScreen
import com.bossmg.android.designsystem.ui.components.MemoCardItem
import com.bossmg.android.designsystem.ui.theme.AppTypography
import com.bossmg.android.designsystem.ui.theme.Background
import com.bossmg.android.designsystem.ui.theme.DP12
import com.bossmg.android.designsystem.ui.theme.DP8
import java.time.LocalDate

@Composable
internal fun Home(
    onMemoItemClick: (Int) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (uiState) {
        HomeUIState.Loading -> {
            LoadingScreen()
        }

        is HomeUIState.Success -> {
            if ((uiState as HomeUIState.Success).uiModels.isNotEmpty()) {
                HomeScreen(uiModels = (uiState as HomeUIState.Success).uiModels, onMemoItemClick)
            } else {
                EmptyScreen()
            }
        }
    }
}

@Composable
private fun HomeScreen(
    uiModels: List<HomeUIModel>,
    onMemoItemClick: (Int) -> Unit
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
            HomeCard(it, onMemoItemClick)
        }
    }
}

@Composable
private fun HomeCard(uiModel: HomeUIModel, onMemoItemClick: (Int) -> Unit) {
    CustomCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onMemoItemClick(uiModel.id)
            }
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

@Composable
private fun EmptyScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.text_empty_title),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = AppTypography.titleMedium
        )

        Spacer(modifier = Modifier.height(DP8))

        Text(
            text = stringResource(R.string.text_empty_body),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = AppTypography.bodyMedium,
        )
    }
}

@Preview
@Composable
private fun EmptyScreenPreview() {
    EmptyScreen()
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
    ) {}
}