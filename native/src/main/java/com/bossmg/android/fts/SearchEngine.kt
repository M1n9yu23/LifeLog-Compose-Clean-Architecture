package com.bossmg.android.fts

interface SearchEngine {
    fun indexDocument(id: Int, title: String, body: String)

    fun removeDocument(id: Int)

    fun search(query: String, limit: Int = 20): List<Int>

    fun rebuildIndex(documents: List<Triple<Int, String, String>>)
}
