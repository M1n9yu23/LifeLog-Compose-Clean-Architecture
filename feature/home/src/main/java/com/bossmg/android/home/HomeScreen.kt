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
package com.bossmg.android.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import com.bossmg.android.designsystem.ui.icons.LifeIcons
import com.bossmg.android.designsystem.ui.theme.AppTypography
import com.bossmg.android.designsystem.ui.theme.DP0
import com.bossmg.android.designsystem.ui.theme.DP12
import com.bossmg.android.designsystem.ui.theme.DP16
import com.bossmg.android.designsystem.ui.theme.DP32
import com.bossmg.android.designsystem.ui.theme.DP8
import com.bossmg.android.designsystem.ui.util.cardColor
import com.bossmg.android.model.MemoItem
import java.time.LocalDate

@Composable
internal fun Home(
    onMemoItemClick: (Int) -> Unit,
    onSettingsClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()

    when (uiState) {
        HomeUIState.Loading -> {
            LoadingScreen()
        }

        is HomeUIState.Success -> {
            HomeScreen(
                uiModels = (uiState as HomeUIState.Success).uiModels,
                searchQuery = searchQuery,
                searchResults = searchResults,
                onSearchQueryChange = viewModel::onSearchQueryChange,
                onMemoItemClick = onMemoItemClick,
                onSettingsClick = onSettingsClick,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreen(
    uiModels: List<MemoItem>,
    searchQuery: String,
    searchResults: List<MemoItem>,
    onSearchQueryChange: (String) -> Unit,
    onMemoItemClick: (Int) -> Unit,
    onSettingsClick: () -> Unit,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .statusBarsPadding(),
    ) {
        SearchBar(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .then(
                        if (!expanded) {
                            Modifier.padding(horizontal = DP16, vertical = DP8)
                        } else {
                            Modifier
                        },
                    ),
            windowInsets = WindowInsets(left = DP0, top = DP0, right = DP0, bottom = DP0),
            expanded = expanded,
            onExpandedChange = { expanded = it },
            inputField = {
                SearchBarDefaults.InputField(
                    query = searchQuery,
                    onQueryChange = onSearchQueryChange,
                    onSearch = {},
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                    placeholder = {
                        Text(
                            text = stringResource(R.string.text_search_placeholder),
                            style = AppTypography.bodyLarge,
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = LifeIcons.Search,
                            contentDescription = null,
                        )
                    },
                    trailingIcon = {
                        if (expanded) {
                            IconButton(
                                onClick = {
                                    if (searchQuery.isNotEmpty()) {
                                        onSearchQueryChange("")
                                    } else {
                                        expanded = false
                                    }
                                },
                            ) {
                                Icon(
                                    imageVector = LifeIcons.Close,
                                    contentDescription = stringResource(R.string.cd_close_search),
                                )
                            }
                        } else {
                            IconButton(onClick = onSettingsClick) {
                                Icon(
                                    imageVector = LifeIcons.Settings,
                                    contentDescription = stringResource(R.string.cd_settings),
                                )
                            }
                        }
                    },
                )
            },
        ) {
            SearchContent(
                searchQuery = searchQuery,
                searchResults = searchResults,
                onMemoItemClick = onMemoItemClick,
            )
        }

        if (!expanded) {
            if (uiModels.isNotEmpty()) {
                LazyColumn(
                    modifier =
                        Modifier
                            .weight(1f)
                            .padding(horizontal = DP12),
                ) {
                    items(uiModels, key = { it.id }) {
                        HomeCard(it, onMemoItemClick)
                    }
                }
            } else {
                EmptyScreen(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun SearchContent(
    searchQuery: String,
    searchResults: List<MemoItem>,
    onMemoItemClick: (Int) -> Unit,
) {
    if (searchQuery.isBlank()) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(DP32),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(R.string.text_search_hint),
                style = AppTypography.bodyMedium,
            )
        }
    } else if (searchResults.isEmpty()) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(DP32),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(R.string.text_search_empty, searchQuery),
                style = AppTypography.bodyMedium,
                textAlign = TextAlign.Center,
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier.padding(horizontal = DP12),
        ) {
            items(searchResults, key = { it.id }) { result ->
                HomeCard(result, onMemoItemClick)
            }
        }
    }
}

@Composable
private fun HomeCard(item: MemoItem, onMemoItemClick: (Int) -> Unit) {
    CustomCard(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable { onMemoItemClick(item.id) }
                .padding(vertical = DP8),
        backgroundColor = cardColor(item.mood),
    ) {
        MemoCardItem(
            date = item.date,
            title = item.title,
            mood = item.mood,
            img = item.img,
        )
    }
}

@Composable
private fun EmptyScreen(modifier: Modifier = Modifier) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.text_empty_title),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = AppTypography.titleMedium,
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
        uiModels =
            listOf(
                MemoItem(id = 1, date = LocalDate.of(2025, 10, 1), title = "오늘의 아침", mood = "행복"),
                MemoItem(id = 2, date = LocalDate.of(2025, 10, 2), title = "점심시간", mood = "피곤"),
                MemoItem(id = 3, date = LocalDate.of(2025, 10, 3), title = "저녁 산책", mood = "편안"),
            ),
        searchQuery = "",
        searchResults = emptyList(),
        onSearchQueryChange = {},
        onMemoItemClick = {},
        onSettingsClick = {},
    )
}
