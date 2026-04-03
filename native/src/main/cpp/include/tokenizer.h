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
