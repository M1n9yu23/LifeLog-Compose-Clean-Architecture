#pragma once

#include <string>
#include <vector>
#include <map>
#include <unordered_map>
#include <cstdint>

namespace fts {

    struct SearchResult {
        int doc_id;
        float score;
    };

    class InvertedIndex {
    public:
        void addDocument(int doc_id, const std::string &title, const std::string &body);

        void removeDocument(int doc_id);

        std::vector<SearchResult> search(const std::string &query, int limit = 20) const;

        bool save(const std::string &path) const;

        bool load(const std::string &path);

        void clear();

        int docCount() const { return static_cast<int>(docs_.size()); }

    private:
        struct Posting {
            int doc_id;
            int term_freq;
        };

        struct DocInfo {
            int total_terms;
        };

        std::map<std::string, std::vector<Posting>> index_;
        std::unordered_map<int, DocInfo> docs_;
        std::unordered_map<std::string, int> doc_freq_;

        std::unordered_map<int, std::vector<std::string>> doc_terms_;

        float termScore(int term_freq, int total_terms, int df) const;

        void indexTokens(int doc_id,
                         const std::vector<std::string> &tokens,
                         int weight,
                         std::unordered_map<std::string, int> &term_counts);

        void buildDocTerms();
    };

}
