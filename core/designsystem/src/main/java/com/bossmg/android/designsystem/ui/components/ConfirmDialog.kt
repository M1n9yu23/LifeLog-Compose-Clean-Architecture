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
package com.bossmg.android.designsystem.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.bossmg.android.designsystem.ui.theme.AppTypography
import com.bossmg.android.designsystem.ui.theme.DP10
import com.bossmg.android.designsystem.ui.theme.DP16
import com.bossmg.android.designsystem.ui.theme.DP24
import com.bossmg.android.designsystem.ui.theme.DP320
import com.bossmg.android.designsystem.ui.theme.DP8

@Composable
fun ConfirmDialog(
    title: String,
    message: String,
    confirmText: String,
    dismissText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties =
            DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
            ),
    ) {
        LifeLogCard(
            shapeTop = DP10,
            shapeBottom = DP10,
            elevation = DP10,
            backgroundColor = MaterialTheme.colorScheme.surface,
        ) {
            Column(
                modifier =
                    Modifier
                        .width(DP320)
                        .padding(DP16)
                        .wrapContentSize(),
            ) {
                Text(
                    text = title,
                    style = AppTypography.titleMedium.copy(color = MaterialTheme.colorScheme.onSurface),
                )

                Spacer(Modifier.height(DP10))

                Text(
                    text = message,
                    style = AppTypography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                )

                Spacer(Modifier.height(DP24))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(
                        onClick = onDismiss,
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onBackground,
                            ),
                    ) {
                        Text(dismissText)
                    }

                    Spacer(Modifier.width(DP8))

                    TextButton(
                        onClick = onConfirm,
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary,
                            ),
                    ) {
                        Text(confirmText)
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun ConfirmDialogPreview() {
    ConfirmDialog(
        title = "언어 변경",
        message = "언어를 변경하시겠습니까? 변경 사항을 적용하려면 앱을 다시 시작해야 합니다.",
        confirmText = "다시 시작",
        dismissText = "취소",
        onConfirm = {},
        onDismiss = {},
    )
}
