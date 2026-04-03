#pragma once

#include <string>
#include <vector>
#include <cstdint>

namespace fts {

    class Tokenizer {
    public:
        static std::vector<std::string> tokenize(const std::string &utf8_text);

    private:
        static std::vector<uint32_t> toCodepoints(const std::string &utf8);

        static std::string codepointToUtf8(uint32_t cp);

        static std::string
        codepointsToUtf8(const std::vector<uint32_t> &cps, size_t start, size_t len);

        static bool isKorean(uint32_t cp);

        static bool isAsciiAlpha(uint32_t cp);

        static bool isAsciiDigit(uint32_t cp);

        static bool isWordChar(uint32_t cp);

        static uint32_t toLowerAscii(uint32_t cp);

        static void addBigrams(const std::vector<uint32_t> &word, std::vector<std::string> &out);
    };

}
