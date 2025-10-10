package com.bossmg.android.photo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.bossmg.android.designsystem.ui.components.LoadingScreen
import com.bossmg.android.designsystem.ui.theme.AppTypography
import com.bossmg.android.designsystem.ui.theme.Background
import com.bossmg.android.designsystem.ui.theme.DP12
import com.bossmg.android.designsystem.ui.theme.DP6
import com.bossmg.android.designsystem.ui.theme.DP8

@Composable
internal fun Photo(
    viewModel: PhotoViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (uiState) {
        PhotoUIState.Loading -> {
            LoadingScreen()
        }

        is PhotoUIState.Success -> {
            if ((uiState as PhotoUIState.Success).uiModel.photos.isNotEmpty()) {
                PhotoScreen((uiState as PhotoUIState.Success).uiModel.photos)
            } else {
                EmptyScreen()
            }
        }
    }
}

@Composable
private fun PhotoScreen(
    photos: List<String> = emptyList()
) {
    LazyVerticalGrid(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Background)
            .statusBarsPadding()
            .padding(DP12),
        columns = GridCells.Fixed(3),
        verticalArrangement = Arrangement.spacedBy(DP6),
        horizontalArrangement = Arrangement.spacedBy(DP6)
    ) {
        items(photos) {
            AsyncImage(
                model = it,
                contentDescription = "사용한 이미지",
                modifier = Modifier.aspectRatio(1f),
                alignment = Alignment.Center,
                contentScale = ContentScale.Crop
            )
        }
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
private fun PhotoScreenPreview() {
    PhotoScreen(List(12) {
        ""
    })
}