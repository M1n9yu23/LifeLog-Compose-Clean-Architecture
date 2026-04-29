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
package com.bossmg.android.auth

import com.bossmg.android.auth.qualifier.WebClientId
import com.bossmg.android.common.safeRunCatching
import com.bossmg.android.domain.model.User
import com.bossmg.android.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class AuthRepositoryImpl @Inject constructor(
    private val dataSource: FirebaseAuthDataSource,
    @WebClientId private val webClientId: String,
) : AuthRepository {
    override fun getCurrentUser(): Flow<User?> =
        dataSource.currentUserFlow().map { it?.toUser() }

    override suspend fun signInWithGoogle(): Result<User> =
        safeRunCatching { dataSource.signInWithGoogle(webClientId).toUser() }

    override suspend fun signOut(): Result<Unit> =
        safeRunCatching { dataSource.signOut() }
}

private fun FirebaseUser.toUser() =
    User(
        uid = uid,
        displayName = displayName,
        email = email,
    )
