package com.bossmg.android.memo

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import com.bossmg.android.designsystem.ui.components.CalendarDialog
import com.bossmg.android.designsystem.ui.components.CustomDivider
import com.bossmg.android.designsystem.ui.components.DefaultTextField
import com.bossmg.android.designsystem.ui.icons.LifeIcons
import com.bossmg.android.designsystem.ui.theme.AppTypography
import com.bossmg.android.designsystem.ui.theme.Background
import com.bossmg.android.designsystem.ui.theme.Black
import com.bossmg.android.designsystem.ui.theme.DP12
import com.bossmg.android.designsystem.ui.theme.DP300
import com.bossmg.android.designsystem.ui.theme.DP8
import com.bossmg.android.designsystem.ui.theme.DarkGray2
import com.bossmg.android.designsystem.ui.theme.Gray5
import java.time.LocalDate

@Composable
fun Memo(
    id: Int = 0
) {
    val uiModel = MemoUIModel(
        MemoItem()
    )

    var showDateDialog by remember { mutableStateOf(false) }

    if (showDateDialog) {
        CalendarDialog(
            uiModel.memoItem.selectedDate
        )
    }

    MemoScreen(uiModel, {
        showDateDialog = it
    })
}

@Composable
private fun MemoScreen(
    uiModel: MemoUIModel,
    onShowDateDialogChange: (Boolean) -> Unit
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
                    uiModel.memoItem.selectedDate,
                    uiModel.memoItem.selectedMood,
                    onShowDateDialogChange,
                    {})

                Spacer(Modifier.height(DP12))

                TitleInputField(uiModel.memoItem.title, {})

                Spacer(Modifier.height(DP8))

                MemoImage(uiModel.memoItem.img)

                Spacer(Modifier.height(DP12))

                CustomDivider()

                DescriptionInputField(uiModel.memoItem.description, {})
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(vertical = DP8),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(onClick = { /* 카메라/갤러리 */ }) {
                Icon(LifeIcons.Photo, contentDescription = stringResource(R.string.icon_camera))
            }
            IconButton(onClick = { /* 공유 기능 */ }) {
                Icon(LifeIcons.Share, contentDescription = stringResource(R.string.icon_share))
            }
            IconButton(onClick = { /* 삭제 */ }) {
                Icon(LifeIcons.Delete, contentDescription = stringResource(R.string.icon_delete))
            }
            IconButton(onClick = { /* 저장 */ }) {
                Icon(LifeIcons.Save, contentDescription = stringResource(R.string.icon_save))
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
    val moods = listOf("😊 기쁨", "😢 슬픔", "😡 화남", "🥱 피곤", "😌 편안")
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
        modifier = Modifier.fillMaxWidth(),
        placeholder = stringResource(R.string.memo_description_placeholder),
    )
}

@Composable
private fun MemoImage(
    img: String?
) {
    if (img != null) {
        AsyncImage(
            model = img,
            contentDescription = stringResource(R.string.memo_image_description),
            modifier = Modifier
                .fillMaxWidth()
                .height(DP300)
        )
    } else {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(DP300)
                .background(color = Black)
        )
    }

//    img?.let {
//        AsyncImage(
//            model = it,
//            contentDescription = "관련 이미지",
//            modifier = Modifier.fillMaxWidth().height(DP400)
//        )
//    }
}

@Preview
@Composable
private fun MemoScreenPreview() {
    MemoScreen(
        MemoUIModel(
            MemoItem()
        ),
        {}
    )
}