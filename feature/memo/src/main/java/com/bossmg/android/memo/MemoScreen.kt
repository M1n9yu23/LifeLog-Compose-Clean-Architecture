package com.bossmg.android.memo

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.bossmg.android.designsystem.ui.components.CalendarDialog
import com.bossmg.android.designsystem.ui.components.CustomDivider
import com.bossmg.android.designsystem.ui.components.DefaultTextField
import com.bossmg.android.designsystem.ui.icons.LifeIcons
import com.bossmg.android.designsystem.ui.theme.AppTypography
import com.bossmg.android.designsystem.ui.theme.Background
import com.bossmg.android.designsystem.ui.theme.DP12
import com.bossmg.android.designsystem.ui.theme.DP300
import com.bossmg.android.designsystem.ui.theme.DP400
import com.bossmg.android.designsystem.ui.theme.DP8
import com.bossmg.android.designsystem.ui.theme.DarkGray2
import com.bossmg.android.designsystem.ui.theme.Gray5
import com.bossmg.android.designsystem.ui.theme.Primary
import com.bossmg.android.domain.util.MoodProvider
import java.time.LocalDate

@Composable
internal fun Memo(
    onBack: () -> Unit,
    id: Int? = null,
    viewModel: MemoViewModel = hiltViewModel()
) {
    val uiModel by viewModel.uiModel.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var showDateDialog by remember { mutableStateOf(false) }
    var showGallery by remember { mutableStateOf(false) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) {
        it?.let {
            context.contentResolver.takePersistableUriPermission(
                it,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )

            viewModel.updateImage(it.toString())
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
            }
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

    MemoScreen(
        uiModel = uiModel,
        onBack = onBack,
        onShowDateDialogChange = { showDateDialog = it },
        onShowGallery = { showGallery = it },
        onTitleChange = { viewModel.updateTitle(it) },
        onDescriptionChange = { viewModel.updateDescription(it) },
        onMoodSelected = { viewModel.updateMood(it) },
        onSaveClick = { viewModel.saveMemo() },
        onDeleteClick = { viewModel.deleteMemo(id) }
    )
}

@Composable
private fun MemoScreen(
    uiModel: MemoUIModel,
    onBack: () -> Unit,
    onShowDateDialogChange: (Boolean) -> Unit,
    onShowGallery: (Boolean) -> Unit,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onMoodSelected: (String) -> Unit,
    onSaveClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .statusBarsPadding()
            .padding(DP12)
    ) {
        LazyColumn {
            item {
                Head(
                    uiModel.selectedDate,
                    uiModel.selectedMood,
                    onShowDateDialogChange,
                    {
                        onMoodSelected(it)
                    })

                Spacer(Modifier.height(DP12))

                TitleInputField(uiModel.title, {
                    onTitleChange(it)
                })

                Spacer(Modifier.height(DP8))

                MemoImage(uiModel.img)

                Spacer(Modifier.height(DP12))

                CustomDivider()

                DescriptionInputField(uiModel.description, {
                    onDescriptionChange(it)
                })
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = DP12)
        ) {
            CustomDivider()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = DP8),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { onShowGallery(true) }) {
                    Icon(
                        LifeIcons.Photo,
                        contentDescription = stringResource(R.string.icon_camera),
                        tint = Primary
                    )
                }
                IconButton(onClick = { /* 공유 기능 */ }) {
                    Icon(
                        LifeIcons.Share,
                        contentDescription = stringResource(R.string.icon_share),
                        tint = Primary
                    )
                }
                IconButton(onClick = {
                    onDeleteClick()
                    onBack()
                }) {
                    Icon(
                        LifeIcons.Delete,
                        contentDescription = stringResource(R.string.icon_delete),
                        tint = Primary
                    )
                }
                IconButton(onClick = {
                    onSaveClick()
                    onBack()
                }) {
                    Icon(
                        LifeIcons.Save,
                        contentDescription = stringResource(R.string.icon_save),
                        tint = Primary
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
    onMoodSelected: (String) -> Unit
) {
    val moods = MoodProvider.Moods.map { it.str }
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = selectedDate.toString(),
            modifier = Modifier.clickable {
                onShowDateDialogChange(true)
            },
            style = AppTypography.bodyLarge
        )

        Box {
            Text(
                text = selectedMood,
                modifier = Modifier
                    .clickable { expanded = true },
                style = AppTypography.bodyLarge
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                moods.forEach { mood ->
                    DropdownMenuItem(
                        text = { Text(mood) },
                        onClick = {
                            onMoodSelected(mood)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun TitleInputField(
    title: String,
    onTitleChange: (String) -> Unit
) {
    DefaultTextField(
        value = title,
        onValueChange = {
            onTitleChange(it)
        },
        modifier = Modifier.fillMaxWidth(),
        placeholder = stringResource(R.string.memo_title_placeholder),
        textStyle = AppTypography.titleLarge.copy(color = DarkGray2),
        hintStyle = AppTypography.titleLarge.copy(color = Gray5)
    )
}

@Composable
private fun DescriptionInputField(
    description: String,
    onDescriptionChange: (String) -> Unit
) {
    DefaultTextField(
        value = description,
        onValueChange = {
            onDescriptionChange(it)
        },
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = DP300),
        placeholder = stringResource(R.string.memo_description_placeholder),
    )
}

@Composable
private fun MemoImage(
    img: String?
) {
    img?.let {
        AsyncImage(
            model = it,
            contentDescription = "관련 이미지",
            modifier = Modifier
                .fillMaxWidth()
                .height(DP400)
        )
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
        {}
    )
}