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
package com.bossmg.android.memo.camera

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.camera.compose.CameraXViewfinder
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import com.bossmg.android.designsystem.ui.icons.LifeIcons
import com.bossmg.android.designsystem.ui.theme.AppTypography
import com.bossmg.android.designsystem.ui.theme.DP1
import com.bossmg.android.designsystem.ui.theme.DP16
import com.bossmg.android.designsystem.ui.theme.DP18
import com.bossmg.android.designsystem.ui.theme.DP24
import com.bossmg.android.designsystem.ui.theme.DP28
import com.bossmg.android.designsystem.ui.theme.DP3
import com.bossmg.android.designsystem.ui.theme.DP32
import com.bossmg.android.designsystem.ui.theme.DP40
import com.bossmg.android.designsystem.ui.theme.DP56
import com.bossmg.android.designsystem.ui.theme.DP6
import com.bossmg.android.designsystem.ui.theme.DP72
import com.bossmg.android.designsystem.ui.theme.DP8
import com.bossmg.android.designsystem.ui.theme.DP80
import com.bossmg.android.memo.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
internal fun CameraScreen(
    onPhotoCaptured: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: CameraViewModel = hiltViewModel(),
) {
    val cameraPermission = rememberPermissionState(Manifest.permission.CAMERA)
    val lifecycleOwner = LocalLifecycleOwner.current
    val surfaceRequest by viewModel.surfaceRequest.collectAsStateWithLifecycle()
    val captureState by viewModel.captureState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val captureFailedMessage = stringResource(R.string.memo_camera_capture_failed)

    BackHandler(enabled = captureState !is CaptureState.Idle) {
        if (captureState is CaptureState.Preview) {
            viewModel.resetCaptureState()
        }
    }

    LaunchedEffect(Unit) {
        if (!cameraPermission.status.isGranted) {
            cameraPermission.launchPermissionRequest()
        }
    }

    LaunchedEffect(cameraPermission.status.isGranted) {
        if (cameraPermission.status.isGranted) {
            viewModel.startCamera(lifecycleOwner)
        }
    }

    LaunchedEffect(captureState) {
        if (captureState is CaptureState.Failure) {
            snackbarHostState.showSnackbar(captureFailedMessage)
            viewModel.resetCaptureState()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is CameraEvent.PhotoConfirmed -> onPhotoCaptured(event.uri)
                CameraEvent.ConfirmFailed -> snackbarHostState.showSnackbar(captureFailedMessage)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent,
    ) { paddingValues ->
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .padding(paddingValues),
        ) {
            when (val state = captureState) {
                is CaptureState.Preview -> {
                    PhotoPreview(
                        absolutePath = state.absolutePath,
                        onConfirm = viewModel::confirmCapture,
                        onRetake = viewModel::resetCaptureState,
                    )
                }

                else -> {
                    when {
                        cameraPermission.status.isGranted -> {
                            surfaceRequest?.let { request ->
                                CameraXViewfinder(
                                    surfaceRequest = request,
                                    modifier = Modifier.fillMaxSize(),
                                )
                            }
                            IconButton(
                                onClick = onBack,
                                modifier =
                                    Modifier
                                        .align(Alignment.TopStart)
                                        .padding(DP8),
                            ) {
                                Icon(
                                    imageVector = LifeIcons.ArrowLeft,
                                    contentDescription = stringResource(R.string.memo_back),
                                    tint = Color.White,
                                )
                            }
                            Box(
                                modifier =
                                    Modifier
                                        .align(Alignment.BottomCenter)
                                        .fillMaxWidth()
                                        .background(
                                            Brush.verticalGradient(
                                                colors =
                                                    listOf(
                                                        Color.Transparent,
                                                        Color.Black.copy(alpha = 0.5f),
                                                    ),
                                            ),
                                        )
                                        .padding(top = DP24, bottom = DP40),
                                contentAlignment = Alignment.Center,
                            ) {
                                CaptureButton(
                                    isCapturing = state is CaptureState.Capturing,
                                    onClick = viewModel::capturePhoto,
                                )
                            }
                        }

                        else -> {
                            CameraPermissionRequired(
                                shouldShowRationale = cameraPermission.status.shouldShowRationale,
                                onRequest = cameraPermission::launchPermissionRequest,
                                onBack = onBack,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PhotoPreview(
    absolutePath: String,
    onConfirm: () -> Unit,
    onRetake: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        SubcomposeAsyncImage(
            model = absolutePath,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit,
        )
        Spacer(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(DP80)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Black.copy(alpha = 0.5f), Color.Transparent),
                        ),
                    ),
        )
        IconButton(
            onClick = onRetake,
            modifier =
                Modifier
                    .align(Alignment.TopStart)
                    .padding(DP8),
        ) {
            Icon(
                imageVector = LifeIcons.ArrowLeft,
                contentDescription = stringResource(R.string.memo_camera_retake),
                tint = Color.White,
            )
        }
        Column(
            modifier =
                Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.75f)),
                        ),
                    )
                    .padding(top = DP56, bottom = DP32, start = DP24, end = DP24),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedButton(
                    onClick = onRetake,
                    border = BorderStroke(DP1, Color.White),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                ) {
                    Text(
                        text = stringResource(R.string.memo_camera_retake),
                        style = AppTypography.bodyLarge,
                    )
                }
                Button(onClick = onConfirm) {
                    Icon(
                        imageVector = LifeIcons.Save,
                        contentDescription = null,
                        modifier = Modifier.size(DP18),
                    )
                    Spacer(modifier = Modifier.width(DP6))
                    Text(
                        text = stringResource(R.string.memo_camera_use),
                        style = AppTypography.bodyLarge,
                    )
                }
            }
        }
    }
}

@Composable
private fun CaptureButton(
    isCapturing: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val alpha by animateFloatAsState(
        targetValue = if (isCapturing) 0.4f else 1f,
        label = "captureAlpha",
    )
    val density = LocalDensity.current
    val ringStrokeWidth = with(density) { DP3.toPx() }
    val innerRadius = with(density) { DP28.toPx() }
    Box(
        modifier =
            modifier
                .size(DP72)
                .clip(CircleShape)
                .clickable(enabled = !isCapturing, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = Color.White.copy(alpha = alpha),
                radius = size.minDimension / 2f,
                style = Stroke(width = ringStrokeWidth),
            )
            drawCircle(
                color = Color.White.copy(alpha = alpha),
                radius = innerRadius,
            )
        }
    }
}

@Composable
private fun CameraPermissionRequired(
    shouldShowRationale: Boolean,
    onRequest: () -> Unit,
    onBack: () -> Unit,
) {
    val context = LocalContext.current

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(DP32),
        ) {
            Text(
                text =
                    stringResource(
                        if (shouldShowRationale) {
                            R.string.memo_camera_permission_required
                        } else {
                            R.string.memo_camera_permission_denied
                        },
                    ),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Spacer(modifier = Modifier.height(DP16))
            Button(
                onClick =
                    if (shouldShowRationale) {
                        onRequest
                    } else {
                        {
                            val intent =
                                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                    data = Uri.fromParts("package", context.packageName, null)
                                }
                            context.startActivity(intent)
                        }
                    },
            ) {
                Text(
                    text =
                        stringResource(
                            if (shouldShowRationale) {
                                R.string.memo_camera_permission_grant
                            } else {
                                R.string.memo_camera_open_settings
                            },
                        ),
                )
            }
        }
        IconButton(
            onClick = onBack,
            modifier =
                Modifier
                    .align(Alignment.TopStart)
                    .padding(DP8),
        ) {
            Icon(
                imageVector = LifeIcons.ArrowLeft,
                contentDescription = stringResource(R.string.memo_back),
                tint = MaterialTheme.colorScheme.onBackground,
            )
        }
    }
}
