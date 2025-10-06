package com.bossmg.android.mood

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bossmg.android.designsystem.ui.components.CustomCard
import com.bossmg.android.designsystem.ui.components.LoadingScreen
import com.bossmg.android.designsystem.ui.components.MemoCardItem
import com.bossmg.android.designsystem.ui.theme.AppTypography
import com.bossmg.android.designsystem.ui.theme.Background
import com.bossmg.android.designsystem.ui.theme.DP1
import com.bossmg.android.designsystem.ui.theme.DP10
import com.bossmg.android.designsystem.ui.theme.DP12
import com.bossmg.android.designsystem.ui.theme.DP16
import com.bossmg.android.designsystem.ui.theme.DP24
import com.bossmg.android.designsystem.ui.theme.DP8
import com.bossmg.android.designsystem.ui.theme.DP800
import com.bossmg.android.designsystem.ui.theme.DarkGray2
import com.bossmg.android.designsystem.ui.theme.Gray5
import java.time.LocalDate

@Composable
internal fun Mood(
    onMemoItemClick: (Int) -> Unit,
    viewModel: MoodViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.load()
    }

    when {
        uiState.isLoading -> {
            LoadingScreen()
        }

        else -> {
            MoodScreen(
                uiModel = uiState.uiModel,
                selectedMood = uiState.selectedMood,
                onMoodSelected = {
                    viewModel.selectMood(it)
                },
                onMemoItemClick = onMemoItemClick
            )
        }
    }
}

@Composable
private fun MoodScreen(
    uiModel: MoodUIModel,
    selectedMood: String,
    onMoodSelected: (String) -> Unit,
    onMemoItemClick: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .statusBarsPadding()
            .padding(DP12)
    ) {
        item {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = stringResource(R.string.text_mood),
                    style = AppTypography.titleLarge.copy(color = DarkGray2)
                )

                Spacer(Modifier.height(DP16))

                MoodsBox(uiModel.moods, onMoodSelected)

                Spacer(Modifier.height(DP24))
            }
        }

        items(uiModel.memoItem.filter { it.mood.contains(selectedMood) }) {
            MemoItemCard(it, onMemoItemClick)
        }
    }
}


@Composable
private fun MemoItemCard(item: MemoItem, onMemoItemClick: (Int) -> Unit) {
    CustomCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onMemoItemClick(item.id)
            }
            .padding(vertical = DP8),
        backgroundColor = item.cardColor
    ) {
        MemoCardItem(
            date = item.date,
            title = item.title,
            mood = item.mood,
            img = item.img
        )
    }
}

@Composable
private fun MoodsBox(
    moods: Map<String, Int>,
    onMoodSelected: (String) -> Unit
) {
    CustomCard(
        modifier = Modifier
            .fillMaxWidth()
            .border(width = DP1, color = Gray5, shape = RoundedCornerShape(DP10)),
        shapeTop = DP10,
        shapeBottom = DP10,
        elevation = DP10
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = DP800)
                .padding(DP12)
        ) {
            items(moods.entries.toList()) { entry ->
                val emoji = entry.key.split(" ")[0]
                val count = entry.value

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { onMoodSelected(entry.key) }
                        .padding(DP16)
                ) {
                    Text(
                        text = emoji,
                        fontSize = 40.sp
                    )
                    Text(
                        text = count.toString(),
                        fontSize = 30.sp
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun MoodScreenPreview() {
    MoodScreen(
        MoodUIModel(
            moods = mapOf(
                "\uD83D\uDCDD 메모" to 1,
                "\uD83D\uDE0A 기쁨" to 2,
                "\uD83D\uDE22 슬픔" to 3,
                "\uD83D\uDE04 즐거움" to 1,
                "\uD83D\uDE2D 우울" to 2
            ),
            memoItem = listOf(
                MemoItem(
                    id = 1,
                    date = LocalDate.of(2025, 10, 1),
                    title = "오늘의 아침",
                    mood = "\uD83D\uDE0A 기쁨"
                ),
                MemoItem(
                    id = 2,
                    date = LocalDate.of(2025, 10, 2),
                    title = "점심시간",
                    mood = "피곤"
                ),
                MemoItem(
                    id = 3,
                    date = LocalDate.of(2025, 10, 3),
                    title = "저녁 산책",
                    mood = "\uD83D\uDE0A 기쁨",
                    img = "https://picsum.photos/id/237/200/300"
                )
            )
        ),
        "\uD83D\uDE0A 기쁨",
        {}
    ) {

    }
}