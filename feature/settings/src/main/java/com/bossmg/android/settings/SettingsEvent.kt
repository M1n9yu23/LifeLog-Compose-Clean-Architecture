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
package com.bossmg.android.settings

internal sealed interface SettingsEvent {
    data object SignInSuccess : SettingsEvent

    data class SignInFailed(val cause: Throwable?) : SettingsEvent

    data class RestoreFailed(val cause: Throwable?) : SettingsEvent

    data object SignOutSuccess : SettingsEvent

    data class SignOutFailed(val cause: Throwable?) : SettingsEvent

    data object SignOutSyncFailed : SettingsEvent

    data object SyncSuccess : SettingsEvent

    data class SyncFailed(val cause: Throwable?) : SettingsEvent
}
