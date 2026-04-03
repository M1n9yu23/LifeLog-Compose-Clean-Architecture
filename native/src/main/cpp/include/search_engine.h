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
