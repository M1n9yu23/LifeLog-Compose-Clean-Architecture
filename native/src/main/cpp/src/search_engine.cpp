#include "search_engine.h"

namespace fts {

SearchEngine::SearchEngine(const std::string& index_path)
    : index_path_(index_path) {
    index_.load(index_path_);
}

void SearchEngine::indexDocument(int doc_id,
                                  const std::string& title,
                                  const std::string& body) {
    std::unique_lock<std::shared_mutex> lock(mutex_);
    index_.addDocument(doc_id, title, body);
    persist();
}

void SearchEngine::removeDocument(int doc_id) {
    std::unique_lock<std::shared_mutex> lock(mutex_);
    index_.removeDocument(doc_id);
    persist();
}

std::vector<int> SearchEngine::search(const std::string& query, int limit) {
    std::shared_lock<std::shared_mutex> lock(mutex_);
    auto results = index_.search(query, limit);
    std::vector<int> ids;
    ids.reserve(results.size());
    for (auto& r : results) {
        ids.push_back(r.doc_id);
    }
    return ids;
}

void SearchEngine::rebuildIndex(
    const std::vector<std::tuple<int, std::string, std::string>>& documents) {
    std::unique_lock<std::shared_mutex> lock(mutex_);
    index_.clear();
    for (auto& [id, title, body] : documents) {
        index_.addDocument(id, title, body);
    }
    persist();
}

void SearchEngine::persist() {
    index_.save(index_path_);
}

}
