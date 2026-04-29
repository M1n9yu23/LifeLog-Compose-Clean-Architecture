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
package com.bossmg.android.memo

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import com.bossmg.android.designsystem.ui.components.CalendarDialog
import com.bossmg.android.designsystem.ui.components.DefaultTextField
import com.bossmg.android.designsystem.ui.icons.LifeIcons
import com.bossmg.android.designsystem.ui.theme.AppTypography
import com.bossmg.android.designsystem.ui.theme.DP1
import com.bossmg.android.designsystem.ui.theme.DP10
import com.bossmg.android.designsystem.ui.theme.DP12
import com.bossmg.android.designsystem.ui.theme.DP120
import com.bossmg.android.designsystem.ui.theme.DP16
import com.bossmg.android.designsystem.ui.theme.DP20
import com.bossmg.android.designsystem.ui.theme.DP24
import com.bossmg.android.designsystem.ui.theme.DP28
import com.bossmg.android.designsystem.ui.theme.DP300
import com.bossmg.android.designsystem.ui.theme.DP32
import com.bossmg.android.designsystem.ui.theme.DP4
import com.bossmg.android.designsystem.ui.theme.DP6
import com.bossmg.android.designsystem.ui.theme.DP8
import com.bossmg.android.designsystem.ui.theme.LocalLifeLogColors
import com.bossmg.android.designsystem.ui.util.cardColor
import com.bossmg.android.domain.enums.MoodType
import com.bossmg.android.domain.util.MoodProvider
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
internal fun Memo(
    onBack: (String?) -> Unit,
    id: String? = null,
    viewModel: MemoViewModel = hiltViewModel(),
) {
    val uiModel by viewModel.uiModel.collectAsStateWithLifecycle()
    val currentOnBack by rememberUpdatedState(onBack)
    val context = LocalContext.current

    var showDateDialog by remember { mutableStateOf(false) }
    var showGallery by remember { mutableStateOf(false) }

    val galleryLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenMultipleDocuments(),
        ) { uris ->
            val uriStrings =
                uris.map { uri ->
                    try {
                        context.contentResolver.takePersistableUriPermission(
                            uri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION,
                        )
                    } catch (_: SecurityException) {
                    }
                    uri.toString()
                }
            viewModel.addImages(uriStrings)
        }

    if (showDateDialog) {
        CalendarDialog(
            uiModel.selectedDate,
            onConfirm = {
                viewModel.updateDate(it)
                showDateDialog = false
            },
            onCancel = {
                showDateDialog = false
            },
        )
    }

    LaunchedEffect(showGallery) {
        if (showGallery) {
            galleryLauncher.launch(arrayOf("image/*"))
            showGallery = false
        }
    }

    LaunchedEffect(Unit) {
        viewModel.load(id)
    }

    val memoAddedMessage = stringResource(R.string.snackbar_memo_added)
    val memoEditedMessage = stringResource(R.string.snackbar_memo_edited)
    val memoDeletedMessage = stringResource(R.string.snackbar_memo_deleted)

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            val message =
                when (event) {
                    MemoEvent.MemoAdded -> memoAddedMessage
                    MemoEvent.MemoEdited -> memoEditedMessage
                    MemoEvent.MemoDeleted -> memoDeletedMessage
                }
            currentOnBack(message)
        }
    }

    MemoScreen(
        uiModel = uiModel,
        isEditing = id != null,
        onBack = { currentOnBack(null) },
        onShowDateDialogChange = { showDateDialog = it },
        onShowGallery = { showGallery = it },
        onTitleChange = { viewModel.updateTitle(it) },
        onDescriptionChange = { viewModel.updateDescription(it) },
        onMoodSelected = { viewModel.updateMood(it) },
        onSaveClick = { viewModel.saveMemo() },
        onDeleteClick = { viewModel.deleteMemo(id) },
        onRemoveImage = { viewModel.removeImage(it) },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MemoScreen(
    uiModel: MemoUIModel,
    isEditing: Boolean,
    onBack: () -> Unit,
    onShowDateDialogChange: (Boolean) -> Unit,
    onShowGallery: (Boolean) -> Unit,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onMoodSelected: (String) -> Unit,
    onSaveClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onRemoveImage: (String) -> Unit,
) {
    Scaffold(
        topBar = {
            MemoTopBar(
                onBack = onBack,
                onSaveClick = onSaveClick,
                onShowGallery = { onShowGallery(true) },
                onDeleteClick = onDeleteClick,
            )
        },
    ) { paddingValues ->
        LazyColumn(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
        ) {
            item {
                MemoDateMoodRow(
                    selectedDate = uiModel.selectedDate,
                    selectedMood = uiModel.selectedMood,
                    onShowDateDialogChange = onShowDateDialogChange,
                    onMoodSelected = onMoodSelected,
                )

                TitleInputField(
                    title = uiModel.title,
                    onTitleChange = onTitleChange,
                )

                DescriptionInputField(
                    description = uiModel.description,
                    onDescriptionChange = onDescriptionChange,
                )

                ImageStrip(
                    imgs = uiModel.imgs,
                    onRemoveImage = onRemoveImage,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MemoTopBar(
    onBack: () -> Unit,
    onSaveClick: () -> Unit,
    onShowGallery: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    var menuExpanded by remember { mutableStateOf(false) }

    TopAppBar(
        title = {},
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = LifeIcons.ArrowLeft,
                    contentDescription = stringResource(R.string.memo_back),
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
        },
        actions = {
            IconButton(onClick = onSaveClick) {
                Icon(
                    imageVector = LifeIcons.Save,
                    contentDescription = stringResource(R.string.memo_save),
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(DP28),
                )
            }
            Box {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(
                        imageVector = LifeIcons.More,
                        contentDescription = stringResource(R.string.memo_more),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                    containerColor = MaterialTheme.colorScheme.surface,
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = stringResource(R.string.memo_add_photo),
                                style = AppTypography.bodyMedium,
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = LifeIcons.PhotoTab,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        },
                        onClick = {
                            menuExpanded = false
                            onShowGallery()
                        },
                    )
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = stringResource(R.string.icon_delete),
                                style = AppTypography.bodyMedium,
                                color = MaterialTheme.colorScheme.error,
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = LifeIcons.Delete,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                            )
                        },
                        onClick = {
                            menuExpanded = false
                            onDeleteClick()
                        },
                    )
                }
            }
        },
        colors =
            TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background,
            ),
    )
}

@Composable
private fun MemoDateMoodRow(
    selectedDate: LocalDate,
    selectedMood: String,
    onShowDateDialogChange: (Boolean) -> Unit,
    onMoodSelected: (String) -> Unit,
) {
    val formatter = remember { DateTimeFormatter.ofPattern("yyyy년 M월 d일", Locale.KOREAN) }
    val formattedDate = remember(selectedDate) { selectedDate.format(formatter) }

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(cardColor(selectedMood))
                .padding(horizontal = DP16, vertical = DP10),
        horizontalArrangement = Arrangement.spacedBy(DP8),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SuggestionChip(
            onClick = { onShowDateDialogChange(true) },
            label = {
                Text(
                    text = formattedDate,
                    style = AppTypography.bodyMedium,
                )
            },
            icon = {
                Icon(
                    imageVector = LifeIcons.Calendar,
                    contentDescription = null,
                    modifier = Modifier.size(DP16),
                )
            },
            colors =
                SuggestionChipDefaults.suggestionChipColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    labelColor = MaterialTheme.colorScheme.onSurface,
                    iconContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
            border =
                SuggestionChipDefaults.suggestionChipBorder(
                    enabled = true,
                    borderColor = MaterialTheme.colorScheme.outlineVariant,
                ),
        )

        MoodChip(
            selectedMood = selectedMood,
            onMoodSelected = onMoodSelected,
        )
    }
}

@Composable
private fun MoodChip(
    selectedMood: String,
    onMoodSelected: (String) -> Unit,
) {
    val moods = MoodProvider.Moods.map { it.str }
    var expanded by remember { mutableStateOf(false) }

    val moodType =
        remember(selectedMood) {
            MoodProvider.Moods.firstOrNull { it.str == selectedMood }?.type ?: MoodType.MEMO
        }
    val lifeLogColors = LocalLifeLogColors.current
    val strokeColor =
        when (moodType) {
            MoodType.POSITIVE -> lifeLogColors.moodPositiveStroke
            MoodType.NEUTRAL -> lifeLogColors.moodNeutralStroke
            MoodType.NEGATIVE -> lifeLogColors.moodNegativeStroke
            MoodType.MEMO -> lifeLogColors.moodMemoStroke
        }

    Box {
        Row(
            modifier =
                Modifier
                    .clip(RoundedCornerShape(DP8))
                    .background(MaterialTheme.colorScheme.background)
                    .border(DP1, strokeColor, RoundedCornerShape(DP8))
                    .clickable { expanded = true }
                    .padding(horizontal = DP12, vertical = DP6),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(DP4),
        ) {
            Text(
                text = selectedMood,
                style = AppTypography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Icon(
                imageVector = LifeIcons.Mood,
                contentDescription = null,
                modifier = Modifier.size(DP16),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = MaterialTheme.colorScheme.surface,
        ) {
            moods.forEach { mood ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = mood,
                            style = AppTypography.bodyMedium,
                        )
                    },
                    onClick = {
                        onMoodSelected(mood)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
private fun TitleInputField(
    title: String,
    onTitleChange: (String) -> Unit,
) {
    DefaultTextField(
        value = title,
        onValueChange = onTitleChange,
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(start = DP20, end = DP20, top = DP24, bottom = DP8),
        placeholder = stringResource(R.string.memo_title_placeholder),
        textStyle = AppTypography.displayLarge.copy(color = MaterialTheme.colorScheme.onSurface),
        hintStyle = AppTypography.displayLarge.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
    )
}

@Composable
private fun DescriptionInputField(
    description: String,
    onDescriptionChange: (String) -> Unit,
) {
    DefaultTextField(
        value = description,
        onValueChange = onDescriptionChange,
        modifier =
            Modifier
                .fillMaxWidth()
                .heightIn(min = DP300)
                .padding(start = DP20, end = DP20, top = DP4, bottom = DP16),
        placeholder = stringResource(R.string.memo_description_placeholder),
        singleLine = false,
    )
}

@Composable
private fun ImageStrip(
    imgs: List<String>,
    onRemoveImage: (String) -> Unit,
) {
    if (imgs.isEmpty()) return

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(DP8),
        contentPadding = PaddingValues(horizontal = DP16, vertical = DP12),
    ) {
        itemsIndexed(items = imgs, key = { index, uri -> "$index-$uri" }) { _, uri ->
            ImageThumbnail(uri = uri, onRemoveImage = onRemoveImage)
        }
    }
}

@Composable
private fun ImageThumbnail(
    uri: String,
    onRemoveImage: (String) -> Unit,
) {
    Box(
        modifier =
            Modifier
                .size(DP120)
                .clip(RoundedCornerShape(DP12)),
    ) {
        SubcomposeAsyncImage(
            model = uri,
            contentDescription = stringResource(R.string.memo_image_description),
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            loading = {
                Box(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                )
            },
            error = {
                Box(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = LifeIcons.Photo,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(DP32),
                    )
                }
            },
        )
        Box(
            modifier =
                Modifier
                    .padding(DP4)
                    .size(DP24)
                    .align(Alignment.TopEnd)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable { onRemoveImage(uri) },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = LifeIcons.Close,
                contentDescription = stringResource(R.string.memo_remove_image),
                tint = Color.White,
                modifier = Modifier.size(DP16),
            )
        }
    }
}

@Preview
@Composable
private fun MemoScreenPreview() {
    MemoScreen(
        uiModel = MemoUIModel(),
        isEditing = false,
        onBack = {},
        onShowDateDialogChange = {},
        onShowGallery = {},
        onTitleChange = {},
        onDescriptionChange = {},
        onMoodSelected = {},
        onSaveClick = {},
        onDeleteClick = {},
        onRemoveImage = {},
    )
}
