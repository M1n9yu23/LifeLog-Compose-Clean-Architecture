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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bossmg.android.designsystem.ui.theme.AppTypography

@Composable
fun DefaultTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    textStyle: TextStyle = TextStyle.Default,
    hintStyle: TextStyle = TextStyle.Default,
    singleLine: Boolean = true,
) {
    val resolvedTextStyle =
        if (textStyle == TextStyle.Default) {
            AppTypography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface)
        } else {
            textStyle
        }
    val resolvedHintStyle =
        if (hintStyle == TextStyle.Default) {
            AppTypography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            hintStyle
        }
    val cursorColor = MaterialTheme.colorScheme.primary

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = singleLine,
        textStyle = resolvedTextStyle,
        cursorBrush = androidx.compose.ui.graphics.SolidColor(cursorColor),
        modifier =
            modifier
                .fillMaxWidth()
                .padding(8.dp),
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.TopStart,
            ) {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = resolvedHintStyle,
                    )
                }
                innerTextField()
            }
        },
    )
}

@Preview
@Composable
private fun TextFieldPreview() {
    DefaultTextField(
        "",
        {},
        placeholder = "힌트입니다",
    )
}
