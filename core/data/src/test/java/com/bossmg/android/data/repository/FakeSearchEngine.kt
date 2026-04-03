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
