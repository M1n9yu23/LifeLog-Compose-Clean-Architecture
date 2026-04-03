#include "inverted_index.h"
#include "tokenizer.h"

#include <algorithm>
#include <cmath>
#include <cstdio>

namespace fts {

static constexpr uint32_t FILE_MAGIC = 0x4C465431;
static constexpr int TITLE_WEIGHT = 3;

float InvertedIndex::termScore(int term_freq, int total_terms, int df) const {
    if (total_terms == 0 || df <= 0) return 0.0f;
    float tf = static_cast<float>(term_freq) / static_cast<float>(total_terms);
    int N = static_cast<int>(docs_.size());
    float idf = std::log(static_cast<float>(N + 1) / static_cast<float>(df + 1)) + 1.0f;
    return tf * idf;
}

void InvertedIndex::indexTokens(int doc_id,
                                 const std::vector<std::string>& tokens,
                                 int weight,
                                 std::unordered_map<std::string, int>& term_counts) {
    for (const auto& token : tokens) {
        term_counts[token] += weight;
    }
}

void InvertedIndex::addDocument(int doc_id,
                                 const std::string& title,
                                 const std::string& body) {
    removeDocument(doc_id);

    auto title_tokens = Tokenizer::tokenize(title);
    auto body_tokens = Tokenizer::tokenize(body);

    std::unordered_map<std::string, int> term_counts;
    term_counts.reserve(title_tokens.size() + body_tokens.size());
    indexTokens(doc_id, title_tokens, TITLE_WEIGHT, term_counts);
    indexTokens(doc_id, body_tokens, 1, term_counts);

    if (term_counts.empty()) return;

    int total = 0;
    for (auto& [term, cnt] : term_counts) {
        total += cnt;
    }
    docs_[doc_id] = DocInfo{total};

    std::vector<std::string> terms;
    terms.reserve(term_counts.size());
    for (auto& [term, cnt] : term_counts) {
        auto& postings = index_[term];
        postings.push_back(Posting{doc_id, cnt});
        doc_freq_[term]++;
        terms.push_back(term);
    }
    doc_terms_[doc_id] = std::move(terms);
}

void InvertedIndex::removeDocument(int doc_id) {
    auto doc_it = docs_.find(doc_id);
    if (doc_it == docs_.end()) return;
    docs_.erase(doc_it);

    auto terms_it = doc_terms_.find(doc_id);
    if (terms_it != doc_terms_.end()) {
        for (const auto& term : terms_it->second) {
            auto idx_it = index_.find(term);
            if (idx_it == index_.end()) continue;

            auto& postings = idx_it->second;
            postings.erase(
                std::remove_if(postings.begin(), postings.end(),
                               [doc_id](const Posting& p) { return p.doc_id == doc_id; }),
                postings.end());

            auto df_it = doc_freq_.find(term);
            if (df_it != doc_freq_.end()) {
                df_it->second = std::max(0, df_it->second - 1);
            }

            if (idx_it->second.empty()) {
                index_.erase(idx_it);
                doc_freq_.erase(term);
            }
        }
        doc_terms_.erase(terms_it);
    }
}

std::vector<SearchResult> InvertedIndex::search(const std::string& query,
                                                  int limit) const {
    auto query_tokens = Tokenizer::tokenize(query);
    if (query_tokens.empty()) return {};

    std::unordered_map<int, float> scores;
    scores.reserve(docs_.size());

    static constexpr int MAX_PREFIX_TERMS = 64;

    for (const auto& token : query_tokens) {
        int prefix_count = 0;
        auto it = index_.lower_bound(token);
        while (it != index_.end() && prefix_count < MAX_PREFIX_TERMS) {
            if (it->first.size() < token.size() ||
                it->first.compare(0, token.size(), token) != 0) {
                break;
            }

            auto df_it = doc_freq_.find(it->first);
            int df = (df_it != doc_freq_.end()) ? df_it->second : 1;

            for (const auto& posting : it->second) {
                auto doc_it = docs_.find(posting.doc_id);
                if (doc_it == docs_.end()) continue;
                scores[posting.doc_id] += termScore(posting.term_freq,
                                                     doc_it->second.total_terms,
                                                     df);
            }
            ++it;
            ++prefix_count;
        }
    }

    std::vector<SearchResult> results;
    results.reserve(scores.size());
    for (auto& [id, score] : scores) {
        results.push_back(SearchResult{id, score});
    }
    std::sort(results.begin(), results.end(),
              [](const SearchResult& a, const SearchResult& b) {
                  return a.score > b.score;
              });

    if (limit > 0 && static_cast<int>(results.size()) > limit) {
        results.resize(static_cast<size_t>(limit));
    }
    return results;
}

void InvertedIndex::clear() {
    index_.clear();
    docs_.clear();
    doc_freq_.clear();
    doc_terms_.clear();
}

void InvertedIndex::buildDocTerms() {
    doc_terms_.clear();
    for (auto& [term, postings] : index_) {
        for (auto& p : postings) {
            doc_terms_[p.doc_id].push_back(term);
        }
    }
}

bool InvertedIndex::save(const std::string& path) const {
    FILE* f = fopen(path.c_str(), "wb");
    if (!f) return false;

    bool ok = true;
    auto write32 = [&](uint32_t v) { if (fwrite(&v, 4, 1, f) != 1) ok = false; };
    auto write16 = [&](uint16_t v) { if (fwrite(&v, 2, 1, f) != 1) ok = false; };

    write32(FILE_MAGIC);

    write32(static_cast<uint32_t>(docs_.size()));
    for (auto& [id, info] : docs_) {
        write32(static_cast<uint32_t>(id));
        write32(static_cast<uint32_t>(info.total_terms));
    }

    write32(static_cast<uint32_t>(index_.size()));
    for (auto& [term, postings] : index_) {
        write16(static_cast<uint16_t>(term.size()));
        if (fwrite(term.data(), 1, term.size(), f) != term.size()) ok = false;
        write32(static_cast<uint32_t>(postings.size()));
        for (auto& p : postings) {
            write32(static_cast<uint32_t>(p.doc_id));
            write32(static_cast<uint32_t>(p.term_freq));
        }
    }

    fclose(f);
    return ok;
}

bool InvertedIndex::load(const std::string& path) {
    FILE* f = fopen(path.c_str(), "rb");
    if (!f) return false;

    clear();

    auto read32 = [&](uint32_t& v) -> bool { return fread(&v, 4, 1, f) == 1; };
    auto read16 = [&](uint16_t& v) -> bool { return fread(&v, 2, 1, f) == 1; };

    uint32_t magic = 0;
    if (!read32(magic) || magic != FILE_MAGIC) { fclose(f); return false; }

    uint32_t num_docs = 0;
    if (!read32(num_docs)) { fclose(f); return false; }
    docs_.reserve(num_docs);
    for (uint32_t i = 0; i < num_docs; ++i) {
        uint32_t id = 0, total = 0;
        if (!read32(id) || !read32(total)) { fclose(f); return false; }
        docs_[static_cast<int>(id)] = DocInfo{static_cast<int>(total)};
    }

    uint32_t num_terms = 0;
    if (!read32(num_terms)) { fclose(f); return false; }
    doc_freq_.reserve(num_terms);
    for (uint32_t i = 0; i < num_terms; ++i) {
        uint16_t term_len = 0;
        if (!read16(term_len)) { fclose(f); return false; }
        std::string term(term_len, '\0');
        if (fread(term.data(), 1, term_len, f) != term_len) { fclose(f); return false; }

        uint32_t num_postings = 0;
        if (!read32(num_postings)) { fclose(f); return false; }
        std::vector<Posting> postings;
        postings.reserve(num_postings);
        for (uint32_t j = 0; j < num_postings; ++j) {
            uint32_t doc_id = 0, freq = 0;
            if (!read32(doc_id) || !read32(freq)) { fclose(f); return false; }
            postings.push_back(Posting{static_cast<int>(doc_id), static_cast<int>(freq)});
        }
        doc_freq_[term] = static_cast<int>(postings.size());
        index_[term] = std::move(postings);
    }

    fclose(f);
    buildDocTerms();
    return true;
}

}
