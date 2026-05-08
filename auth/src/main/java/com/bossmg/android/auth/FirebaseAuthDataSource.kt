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

import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

internal class FirebaseAuthDataSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val activityProvider: ActivityProvider,
) {
    fun currentUserFlow(): Flow<FirebaseUser?> =
        callbackFlow {
            val listener = FirebaseAuth.AuthStateListener { trySend(it.currentUser) }
            firebaseAuth.addAuthStateListener(listener)
            awaitClose { firebaseAuth.removeAuthStateListener(listener) }
        }

    suspend fun signInWithGoogle(webClientId: String): FirebaseUser {
        val activity =
            activityProvider.getActivity()
                ?: throw IllegalStateException("No foreground Activity available for sign-in")
        val googleIdOption =
            GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(webClientId)
                .build()
        val request =
            GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()
        val credentialManager = CredentialManager.create(activity)
        val response = credentialManager.getCredential(activity, request)
        val googleCredential = GoogleIdTokenCredential.createFrom(response.credential.data)
        val firebaseCredential = GoogleAuthProvider.getCredential(googleCredential.idToken, null)
        return firebaseAuth.signInWithCredential(firebaseCredential).await().user
            ?: throw IllegalStateException("Firebase user is null after sign-in")
    }

    suspend fun signOut() = firebaseAuth.signOut()
}
