#pragma once

#include "inverted_index.h"
#include <string>
#include <vector>
#include <tuple>
#include <shared_mutex>

namespace fts {

class SearchEngine {
public:
    explicit SearchEngine(const std::string& index_path);

    void indexDocument(int doc_id, const std::string& title, const std::string& body);
    void removeDocument(int doc_id);
    std::vector<int> search(const std::string& query, int limit = 20);
    void rebuildIndex(const std::vector<std::tuple<int, std::string, std::string>>& documents);

private:
    std::string index_path_;
    InvertedIndex index_;
    mutable std::shared_mutex mutex_;

    void persist();
};

}
