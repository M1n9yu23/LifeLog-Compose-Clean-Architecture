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
package com.bossmg.android.fts

import java.io.Closeable

class NativeSearchEngine(indexPath: String) : SearchEngine, Closeable {
    private var handle: Long = nativeCreate(indexPath)

    init {
        check(handle != 0L) { "Failed to initialise native FTS engine at: $indexPath" }
    }

    override fun indexDocument(id: Int, title: String, body: String) {
        nativeIndexDocument(handle, id, title, body)
    }

    override fun removeDocument(id: Int) {
        nativeRemoveDocument(handle, id)
    }

    override fun search(query: String, limit: Int): List<Int> {
        if (query.isBlank()) return emptyList()
        return nativeSearch(handle, query, limit).toList()
    }

    override fun rebuildIndex(documents: List<Triple<Int, String, String>>) {
        val ids = IntArray(documents.size) { documents[it].first }
        val titles = Array(documents.size) { documents[it].second }
        val bodies = Array(documents.size) { documents[it].third }
        nativeRebuildIndex(handle, ids, titles, bodies)
    }

    override fun close() {
        if (handle != 0L) {
            nativeDestroy(handle)
            handle = 0L
        }
    }

    private external fun nativeCreate(indexPath: String): Long

    private external fun nativeDestroy(handle: Long)

    private external fun nativeIndexDocument(handle: Long, id: Int, title: String, body: String)

    private external fun nativeRemoveDocument(handle: Long, id: Int)

    private external fun nativeSearch(handle: Long, query: String, limit: Int): IntArray

    private external fun nativeRebuildIndex(
        handle: Long,
        ids: IntArray,
        titles: Array<String>,
        bodies: Array<String>,
    )

    companion object {
        init {
            System.loadLibrary("lifelog_fts")
        }
    }
}
