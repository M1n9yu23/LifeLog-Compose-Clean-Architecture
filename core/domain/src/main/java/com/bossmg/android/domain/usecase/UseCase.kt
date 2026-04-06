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
package com.bossmg.android.domain.usecase

import kotlinx.coroutines.flow.Flow

abstract class FlowUseCase<in Params, out T> {
    protected abstract fun execute(params: Params): Flow<T>

    operator fun invoke(params: Params): Flow<T> = execute(params)
}

abstract class NoParamFlowUseCase<out T> {
    protected abstract fun execute(): Flow<T>

    operator fun invoke(): Flow<T> = execute()
}

abstract class SuspendUseCase<in Params, out T> {
    protected abstract suspend fun execute(params: Params): T

    suspend operator fun invoke(params: Params): T = execute(params)
}
