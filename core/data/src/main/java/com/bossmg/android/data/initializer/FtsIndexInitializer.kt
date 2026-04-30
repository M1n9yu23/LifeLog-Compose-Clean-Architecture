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
package com.bossmg.android.data.initializer

import com.bossmg.android.common.di.IoDispatcher
import com.bossmg.android.data.di.ApplicationScope
import com.bossmg.android.domain.repository.LifeLogReadRepository
import com.gyugle.hanfts.Document
import com.gyugle.hanfts.SearchEngine
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

class FtsIndexInitializer @Inject constructor(
    private val readRepository: LifeLogReadRepository,
    private val searchEngine: SearchEngine,
    @ApplicationScope private val scope: CoroutineScope,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) {
    fun initialize() {
        scope.launch(ioDispatcher) {
            runCatching {
                val docs =
                    readRepository.getLifeLogs().first()
                        .map { Document(id = it.id.toLong(), title = it.title, body = it.description) }
                searchEngine.rebuildIndex(docs)
            }
        }
    }
}
