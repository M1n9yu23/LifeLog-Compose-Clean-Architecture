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

import com.bossmg.android.fts.SearchEngine

class FakeSearchEngine : SearchEngine {
    private val index = mutableMapOf<Int, Pair<String, String>>()

    override fun indexDocument(id: Int, title: String, body: String) {
        index[id] = title to body
    }

    override fun removeDocument(id: Int) {
        index.remove(id)
    }

    override fun search(query: String, limit: Int): List<Int> =
        index
            .filter { (_, pair) ->
                pair.first.contains(query, ignoreCase = true) ||
                    pair.second.contains(query, ignoreCase = true)
            }.keys
            .take(limit)

    override fun rebuildIndex(documents: List<Triple<Int, String, String>>) {
        index.clear()
        documents.forEach { (id, title, body) -> index[id] = title to body }
    }
}
