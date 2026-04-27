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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.bossmg.android.designsystem.ui.components.CustomDivider
import com.bossmg.android.designsystem.ui.components.DefaultTextField
import com.bossmg.android.designsystem.ui.icons.LifeIcons
import com.bossmg.android.designsystem.ui.theme.AppTypography
import com.bossmg.android.designsystem.ui.theme.DP1
import com.bossmg.android.designsystem.ui.theme.DP10
import com.bossmg.android.designsystem.ui.theme.DP100
import com.bossmg.android.designsystem.ui.theme.DP12
import com.bossmg.android.designsystem.ui.theme.DP16
import com.bossmg.android.designsystem.ui.theme.DP24
import com.bossmg.android.designsystem.ui.theme.DP300
import com.bossmg.android.designsystem.ui.theme.DP32
import com.bossmg.android.designsystem.ui.theme.DP4
import com.bossmg.android.designsystem.ui.theme.DP8
import com.bossmg.android.domain.util.MoodProvider
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
internal fun Memo(
    onBack: (String?) -> Unit,
    id: Int? = null,
    viewModel: MemoViewModel = hiltViewModel(),
) {
    val uiModel by viewModel.uiModel.collectAsStateWithLifecycle()
    val currentOnBack by rememberUpdatedState(onBack)
    val context = LocalContext.current

    var showDateDialog by remember { mutableStateOf(false) }
    var showGallery by remember { mutableStateOf(false) }

    val galleryLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetMultipleContents(),
        ) { uris ->
            uris.forEach { uri ->
                try {
                    context.contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION,
                    )
                } catch (_: SecurityException) {
                }
                viewModel.addImage(uri.toString())
            }
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
            galleryLauncher.launch("image/*")
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

@Composable
private fun MemoScreen(
    uiModel: MemoUIModel,
    onShowDateDialogChange: (Boolean) -> Unit,
    onShowGallery: (Boolean) -> Unit,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onMoodSelected: (String) -> Unit,
    onSaveClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onRemoveImage: (String) -> Unit,
) {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .statusBarsPadding()
                .padding(DP12),
    ) {
        LazyColumn {
            item {
                Head(
                    uiModel.selectedDate,
                    uiModel.selectedMood,
                    onShowDateDialogChange,
                    {
                        onMoodSelected(it)
                    },
                )

                Spacer(Modifier.height(DP12))

                TitleInputField(uiModel.title, {
                    onTitleChange(it)
                })

                Spacer(Modifier.height(DP8))

                ImageStrip(
                    imgs = uiModel.imgs,
                    onAddClick = { onShowGallery(true) },
                    onRemoveImage = onRemoveImage,
                )

                CustomDivider()

                DescriptionInputField(uiModel.description, {
                    onDescriptionChange(it)
                })
            }
        }

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = DP12),
        ) {
            CustomDivider()

            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = DP8),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                IconButton(onClick = { onShowGallery(true) }) {
                    Icon(
                        LifeIcons.Photo,
                        contentDescription = stringResource(R.string.icon_camera),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
                IconButton(onClick = { /* 공유 기능 */ }) {
                    Icon(
                        LifeIcons.Share,
                        contentDescription = stringResource(R.string.icon_share),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
                IconButton(onClick = { onDeleteClick() }) {
                    Icon(
                        LifeIcons.Delete,
                        contentDescription = stringResource(R.string.icon_delete),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
                IconButton(onClick = { onSaveClick() }) {
                    Icon(
                        LifeIcons.Save,
                        contentDescription = stringResource(R.string.icon_save),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }
}

@Composable
private fun Head(
    selectedDate: LocalDate,
    selectedMood: String,
    onShowDateDialogChange: (Boolean) -> Unit,
    onMoodSelected: (String) -> Unit,
) {
    val moods = MoodProvider.Moods.map { it.str }
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = selectedDate.toString(),
            modifier =
                Modifier.clickable {
                    onShowDateDialogChange(true)
                },
            style = AppTypography.bodyLarge,
        )

        Box {
            Text(
                text = selectedMood,
                modifier =
                    Modifier
                        .clickable { expanded = true },
                style = AppTypography.bodyLarge,
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                moods.forEach { mood ->
                    DropdownMenuItem(
                        text = { Text(mood) },
                        onClick = {
                            onMoodSelected(mood)
                            expanded = false
                        },
                    )
                }
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
        onValueChange = {
            onTitleChange(it)
        },
        modifier = Modifier.fillMaxWidth(),
        placeholder = stringResource(R.string.memo_title_placeholder),
        textStyle = AppTypography.titleLarge.copy(color = MaterialTheme.colorScheme.onSurface),
        hintStyle = AppTypography.titleLarge.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
    )
}

@Composable
private fun DescriptionInputField(
    description: String,
    onDescriptionChange: (String) -> Unit,
) {
    DefaultTextField(
        value = description,
        onValueChange = {
            onDescriptionChange(it)
        },
        modifier =
            Modifier
                .fillMaxWidth()
                .heightIn(min = DP300),
        placeholder = stringResource(R.string.memo_description_placeholder),
        singleLine = false,
    )
}

@Composable
private fun ImageStrip(
    imgs: List<String>,
    onAddClick: () -> Unit,
    onRemoveImage: (String) -> Unit,
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(DP8),
        contentPadding = PaddingValues(vertical = DP8),
    ) {
        itemsIndexed(items = imgs) { _, uri ->
            Box(
                modifier =
                    Modifier
                        .size(DP100)
                        .clip(RoundedCornerShape(DP10)),
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

        item {
            Box(
                modifier =
                    Modifier
                        .size(DP100)
                        .clip(RoundedCornerShape(DP10))
                        .border(
                            width = DP1,
                            color = MaterialTheme.colorScheme.outlineVariant,
                            shape = RoundedCornerShape(DP10),
                        )
                        .clickable { onAddClick() },
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Icon(
                        imageVector = LifeIcons.Photo,
                        contentDescription = stringResource(R.string.memo_add_image),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(DP32),
                    )
                    Spacer(Modifier.height(DP4))
                    Text(
                        text = stringResource(R.string.memo_add_image),
                        style =
                            AppTypography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            ),
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun MemoScreenPreview() {
    MemoScreen(
        MemoUIModel(),
        {},
        {},
        {},
        {},
        {},
        {},
        {},
        {},
    )
}
