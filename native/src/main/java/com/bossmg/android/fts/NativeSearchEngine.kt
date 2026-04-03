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
