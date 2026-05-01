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
package com.bossmg.android.sync.remote

import com.bossmg.android.common.safeRunCatching
import com.bossmg.android.sync.model.LifeLogRemoteDto
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

internal class FirestoreDataSource @Inject constructor(
    private val firestore: FirebaseFirestore,
) {
    suspend fun batchUpsertLogs(uid: String, dtos: List<LifeLogRemoteDto>): Result<Unit> =
        safeRunCatching {
            dtos.chunked(MAX_BATCH_SIZE).forEach { chunk ->
                val batch = firestore.batch()
                chunk.forEach { dto ->
                    batch.set(userLogsCollection(uid).document(dto.id), dto)
                }
                batch.commit().await()
            }
        }

    suspend fun batchDeleteLogs(uid: String, ids: List<String>): Result<Unit> =
        safeRunCatching {
            ids.chunked(MAX_BATCH_SIZE).forEach { chunk ->
                val batch = firestore.batch()
                chunk.forEach { id ->
                    batch.delete(userLogsCollection(uid).document(id))
                }
                batch.commit().await()
            }
        }

    suspend fun getAllLogs(uid: String): Result<List<LifeLogRemoteDto>> =
        safeRunCatching {
            userLogsCollection(uid)
                .get(Source.SERVER)
                .await()
                .documents
                .mapNotNull { doc ->
                    LifeLogRemoteDto(
                        id = doc.getString("id") ?: return@mapNotNull null,
                        date = doc.getString("date") ?: "",
                        title = doc.getString("title") ?: "",
                        description = doc.getString("description") ?: "",
                        mood = doc.getString("mood") ?: "",
                        updatedAt = doc.getLong("updatedAt") ?: 0L,
                    )
                }
        }

    private fun userLogsCollection(uid: String) =
        firestore.collection("users").document(uid).collection("lifelogs")

    private companion object {
        const val MAX_BATCH_SIZE = 500
    }
}
