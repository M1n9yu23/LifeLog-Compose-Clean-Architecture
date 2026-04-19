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
package com.bossmg.android.data.repository

import com.gyugle.hanfts.Document
import com.gyugle.hanfts.SearchEngine
import com.gyugle.hanfts.SearchResult

class FakeSearchEngine : SearchEngine {
    private val index = mutableMapOf<Long, Pair<String, String>>()

    override val documentCount: Int get() = index.size

    override fun indexDocument(id: Long, title: String, body: String) {
        index[id] = title to body
    }

    override fun indexDocument(document: Document) {
        index[document.id] = document.title to document.body
    }

    override fun removeDocument(id: Long) {
        index.remove(id)
    }

    override fun clear() {
        index.clear()
    }

    override fun search(query: String, limit: Int): List<SearchResult> =
        index
            .filter { (_, pair) ->
                pair.first.contains(query, ignoreCase = true) ||
                    pair.second.contains(query, ignoreCase = true)
            }
            .map { (id, _) -> SearchResult(id = id, score = 1f) }
            .take(limit)

    override fun rebuildIndex(documents: List<Document>) {
        index.clear()
        documents.forEach { index[it.id] = it.title to it.body }
    }

    override fun close() = Unit
}
