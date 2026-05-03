package com.bossmg.android.memo.camera

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import kotlin.coroutines.resume

@ViewModelScoped
internal class CaptureController @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val imageCapture = ImageCapture.Builder()
        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
        .build()

    val preview: Preview = Preview.Builder().build()

    suspend fun bindToLifecycle(lifecycleOwner: LifecycleOwner) {
        val provider = ProcessCameraProvider.awaitInstance(context)
        provider.bindToLifecycle(
            lifecycleOwner,
            CameraSelector.DEFAULT_BACK_CAMERA,
            preview,
            imageCapture,
        )
        try {
            awaitCancellation()
        } finally {
            provider.unbindAll()
        }
    }

    suspend fun capturePhoto(): Result<String> = suspendCancellableCoroutine { cont ->
        val file = File(context.cacheDir, "lifelog_${System.currentTimeMillis()}.jpg")
        val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    cont.resume(Result.success(file.absolutePath))
                }

                override fun onError(e: ImageCaptureException) {
                    cont.resume(Result.failure(e))
                }
            },
        )
    }

    suspend fun confirmCapture(absolutePath: String): Result<String> = withContext(Dispatchers.IO) {
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "LifeLog_${System.currentTimeMillis()}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/LifeLog")
            }
        }
        val uri = context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues,
        ) ?: return@withContext Result.failure(IllegalStateException("MediaStore insert failed"))
        runCatching {
            context.contentResolver.openOutputStream(uri)!!.use { out ->
                File(absolutePath).inputStream().use { it.copyTo(out) }
            }
            File(absolutePath).delete()
            uri.toString()
        }.onFailure {
            runCatching { context.contentResolver.delete(uri, null, null) }
        }
    }
}
