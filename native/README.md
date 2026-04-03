# Lifelog-fts

> **참고:** 이 모듈은 현재 [LifeLog](https://github.com/M1n9yu23/LifeLog-Compose-Clean-Architecture) 프로젝트 내부 모듈로 포함되어 있습니다.  
> 추후 독립적인 오픈소스 라이브러리로 별도 저장소에 배포될 예정이며, 정식 배포 전까지는 독립적인 사용을 권장하지 않습니다.

Android NDK(C++17)로 구현된 경량 **전문 검색(Full-Text Search) 엔진**입니다.  
외부 라이브러리 의존 없이 **한국어·영어** 텍스트를 지원하며, 역색인 기반의 영속 검색 인덱스를 제공합니다.

```
"오늘 산책했다 Good morning" → index → search("산책") → [docId: 42]
```

## 알고리즘 및 기술

| 분류 | 항목 |
|---|---|
| **정보 검색** | 역색인(Inverted Index), TF-IDF 스코어링 |
| **언어 처리** | 바이그램(Bigram) 토크나이저, Unicode 코드포인트 기반 문자 분류 |
| **자료구조** | `std::map` (정렬 트리, 프리픽스 스캔용), `std::unordered_map` (O(1) 조회), 역맵(doc → terms) |
| **동시성** | Readers-Writer Lock (`std::shared_mutex` / C++17) |
| **인코딩** | 수동 UTF-8 디코더 (1~4바이트 시퀀스) |
| **직렬화** | 커스텀 바이너리 포맷 (매직 바이트 + 리틀엔디언) |
| **언어 / 플랫폼** | C++17, Android NDK, JNI, Kotlin |
| **빌드** | CMake 3.22.1, Gradle (Kotlin DSL) |

## 특징

| | |
|---|---|
| **한국어 바이그램 토크나이저** | 형태소 분석기 없이 유니코드 코드포인트 기반 2-gram 토큰 생성 |
| **영어 정규화** | 단어 단위, 대소문자 무시 |
| **혼합 언어 지원** | 동일 텍스트 내 한국어·영어 각 언어 전략 독립 적용 |
| **TF-IDF 랭킹** | 필드 가중치 스코어링 (제목 3×, 본문 1×) |
| **프리픽스 검색** | `std::map::lower_bound`를 활용한 실시간 접두어 매칭 |
| **영속 인덱스** | 컴팩트 바이너리 직렬화 — 앱 재시작 후 재색인 불필요 |
| **스레드 안전** | `std::shared_mutex`: 읽기 동시 허용, 쓰기 배타적 접근 |
| **외부 의존 없음** | 순수 C++17 STL만 사용. Lucene, SQLite FTS5, 서드파티 라이브러리 없음 |

## 왜 C++ NDK인가?

각 대안을 검토한 결과는 다음과 같습니다.

| 방식 | 문제점 |
|---|---|
| **Room FTS5 (SQLite)** | 한국어 토크나이저 미내장. 별도 ICU 설정 없이는 공백 기준 분리만 가능 — `"오늘산책"` 은 단일 토큰으로 처리되어 부분 매칭 불가 |
| **외부 검색 라이브러리** | APK 수 MB 증가, 라이선스 제약, 한국어 플러그인 없이는 여전히 동일 문제 |
| **순수 Kotlin 구현** | 대용량 색인 빌드 시 GC 일시 중단 발생; JVM 힙 상한으로 포스팅 리스트가 예측 불가능 |
| **C++17 NDK (채택)** | UTF-8 디코더, 바이그램 로직, 바이너리 직렬화를 STL만으로 600줄 내에 구현 가능. GC 없음. 예측 가능한 메모리. 단일 순차 I/O 패스로 디스크에서 로드 |

## 아키텍처

```
NativeSearchEngine.kt          ← Kotlin 진입점 (SearchEngine 인터페이스 구현)
        │
        │  JNI  (lifelog_fts.so)
        ▼
fts_jni.cpp                    ← JNI 브릿지 — 포인터-핸들 패턴, 예외 안전
        │
        ▼
fts::SearchEngine              ← 공개 C++ API + std::shared_mutex 동시성 보호
        │
        ├── fts::InvertedIndex ← TF-IDF 포스팅 리스트, 바이너리 영속화, 프리픽스 스캔
        │
        └── fts::Tokenizer     ← UTF-8 → 코드포인트 → 언어별 토큰
```

Kotlin `SearchEngine` 인터페이스는 의존성 역전을 위해 별도 분리되었습니다.  
프로덕션은 `NativeSearchEngine`을 주입하고, 테스트는 `FakeSearchEngine`으로 대체합니다.

## 설치

표준 Android 라이브러리 모듈로 프로젝트에 추가합니다.

```kotlin
// settings.gradle.kts
include(":native")
```

```kotlin
// app/build.gradle.kts
dependencies {
    implementation(project(":native"))
}
```

NDK 빌드는 `arm64-v8a`, `armeabi-v7a`, `x86_64` 세 ABI를 타겟으로 합니다.  
C++17 이상 필요 (`-std=c++17`, `ANDROID_STL=c++_shared`).

## 사용법

```kotlin
// 생성 — 인덱스 파일은 앱 내부 저장소에 위치
val engine: SearchEngine = NativeSearchEngine(
    indexPath = context.filesDir.resolve("fts.bin").absolutePath
)

// 문서 색인
engine.indexDocument(id = 42, title = "오늘의 산책", body = "날씨가 맑아서 공원을 걸었다.")

// 검색 — TF-IDF 스코어 기준 내림차순 doc ID 목록 반환
val results: List<Int> = engine.search("산책")   // → [42]

// 프리픽스 검색 (별도 설정 불필요)
engine.search("산")    // "산책", "산보", … 에 매칭 (최대 64개 프리픽스 텀)
engine.search("go")   // "good", "goal", … 에 매칭

// 문서 삭제
engine.removeDocument(id = 42)

// 전체 재색인 (DB 마이그레이션 등)
engine.rebuildIndex(
    documents = listOf(
        Triple(1, "제목", "본문"),
        Triple(2, "Title", "Body text"),
    )
)

// 네이티브 메모리 해제 (생명주기 또는 DI 스코프에 맞춰 호출)
(engine as? Closeable)?.close()
```

인덱스는 **매 쓰기 작업 후 자동으로 영속화**됩니다.  
다음 `NativeSearchEngine(path)` 호출 시 바이너리 파일을 즉시 로드하므로 재색인이 필요 없습니다.

## 내부 구현

### 1. Tokenizer

`Tokenizer::tokenize()`는 UTF-8 문자열을 중복 제거된 토큰 목록으로 변환합니다.

**1단계 — UTF-8 → 유니코드 코드포인트**

1~4바이트 시퀀스를 수동으로 디코딩합니다. 잘못된 바이트 시퀀스는 예외 없이 건너뜁니다.  
로케일·플랫폼 의존성이 없습니다.

**2단계 — 단어 청크 분리**

`isWordChar()` (한글 음절/자모, ASCII 알파, ASCII 숫자) 를 만족하는 연속 코드포인트를 하나의 청크로 묶습니다.  
공백, 구두점, 이모지는 구분자로 작동합니다.

한국어 유니코드 범위:
```
U+AC00–U+D7A3  한글 음절    (가–힣)
U+1100–U+11FF  한글 자모
U+3130–U+318F  한글 호환 자모
U+A960–U+A97F  한글 자모 확장-A
U+D7B0–U+D7FF  한글 자모 확장-B
```

**3단계 — 언어별 토큰 생성**

| 청크 유형 | 전략 | 예시 |
|---|---|---|
| 한국어 포함 | 코드포인트 기준 **바이그램** | `"오늘산책"` (4자) → `["오늘","늘산","산책","오늘산책"]` |
| 한국어 1글자 | 단독 토큰 | `"책"` → `["책"]` |
| 5자 이상 한국어 | 바이그램만 (전체 단어 토큰 없음) | `"오늘산책했다"` → `["오늘","늘산","산책","책했","했다"]` |
| ASCII 알파/숫자 | 소문자 단어 단위 | `"Good"` → `["good"]` |

> **4자 이하 단어 전체 토큰**  
> 바이그램 외에 **전체 단어도 토큰으로 추가**합니다 (`addBigrams`의 `word.size() <= 4` 조건).  
> 2~4자 단어는 바이그램 토큰과 함께 전체 단어 토큰도 생성됩니다.

> **혼합 언어 처리**  
> 공백으로 분리된 경우 각 청크가 독립적으로 처리됩니다.  
> `"meeting 미팅"` → `"meeting"` 청크(ASCII) + `"미팅"` 청크(한국어) → `["meeting", "미팅"]`  
>
> 만약 공백 없이 한국어·ASCII가 한 청크에 섞인 경우 (`"abc가나다"`), `hasKorean=true`이므로  
> **전체 코드포인트**에 바이그램이 적용됩니다 → `["ab","bc","c가","가나","나다"]`.

**4단계 — 중복 제거**

`unordered_set`으로 중복 토큰을 필터링 후 반환합니다.

### 2. InvertedIndex와 TF-IDF 스코어링

인덱스의 핵심 자료구조는 `std::map<string, vector<Posting>>`입니다.  
정렬된 `std::map`을 선택한 이유는 아래의 프리픽스 스캔을 위해서입니다.

**문서 색인 과정**

```
addDocument(doc_id, title, body)
  1. removeDocument(doc_id)           — 멱등적 upsert 보장
  2. tokenize(title) × TITLE_WEIGHT(3)
     tokenize(body)  × 1
  3. term_counts 맵에 가중치 적용 빈도 합산
  4. index_[term]에 Posting{doc_id, freq} 삽입
  5. doc_freq_[term]++ 갱신
  6. doc_terms_[doc_id] = [해당 문서의 모든 텀]  — 빠른 삭제를 위한 역맵
```

`doc_terms_` 역맵 덕분에 `removeDocument`는 인덱스 전체를 스캔하지 않고,  
해당 문서가 기여한 텀만 O(terms_per_doc)에 정리합니다.

**TF-IDF 스코어 계산**

```
TF  = 문서 내 해당 텀 빈도 / 문서 전체 텀 수
IDF = log( (N + 1) / (df + 1) ) + 1        (스무딩, 항상 ≥ 1)

Score = TF × IDF
```

`N` = 전체 문서 수, `df` = 해당 텀을 포함하는 문서 수.  
스코어 내림차순 정렬 후 `limit` 컷오프를 적용합니다.

### 3. 프리픽스 검색

각 쿼리 토큰에 대해 `std::map::lower_bound(token)`으로 시작 위치를 찾은 뒤,  
이터레이터를 앞으로 이동하며 `entry.key.starts_with(token)` 조건을 확인합니다.  
토큰당 최대 **64개의 매칭 텀**으로 상한을 두어 단글자 쿼리의 폭발적 확장을 방지합니다.

한 글자 입력 시 해당 접두어로 시작하는 텀을 순서대로 탐색합니다.  
UTF-8 바이트 구조상 한국어도 동일하게 동작합니다 (`"산"` → `"산책"`, `"산보"` 매칭).

### 4. 바이너리 직렬화 포맷

매직 바이트 `0x4C465431` (`"LFT1"`)으로 파일 정합성을 검사합니다.  
모든 수치는 리틀엔디언으로 저장되며, `load()` 시 `reserve()`를 활용한 단일 순차 I/O 패스로 로드합니다.

```
┌──────────────────────────────────────────────────────────────┐
│ magic           : u32  = 0x4C465431  ("LFT1")                │
│ doc_count       : u32                                        │
│ ── 문서 메타데이터 (doc_count개) ────────────────────────── │
│   doc_id        : u32                                        │
│   total_terms   : u32                                        │
│ term_count      : u32                                        │
│ ── 텀 포스팅 리스트 (term_count개) ──────────────────────── │
│   term_len      : u16                                        │
│   term          : u8[term_len]  (UTF-8)                      │
│   posting_count : u32                                        │
│   ── 포스팅 (posting_count개) ────────────────────────────  │
│     doc_id      : u32                                        │
│     term_freq   : u32                                        │
└──────────────────────────────────────────────────────────────┘
```

### 5. 스레드 안전성

`fts::SearchEngine`은 `InvertedIndex`를 `std::shared_mutex`로 감쌉니다.

```
search()          →  std::shared_lock   (다중 동시 읽기 허용)
indexDocument()   →  std::unique_lock   (배타적 — 모든 읽기·쓰기 대기)
removeDocument()  →  std::unique_lock
rebuildIndex()    →  std::unique_lock
```

`persist()`는 `unique_lock` 안에서 호출되므로 디스크의 파일은 항상 메모리 상태와 일치합니다.

### 6. JNI 브릿지

`NativeSearchEngine`은 C++ 엔진을 `jlong` 핸들(포인터를 `reinterpret_cast`)로 관리합니다.  
모든 JNI 함수는 `extern "C"`로 선언되며, 각 진입점은 `try { ... } catch (...)` + `__android_log_print`로 감싸져 있어 네이티브 예외가 JVM 크래시로 전파되지 않습니다.

**주요 구현 결정:**

- `nativeRebuildIndex`에서 `jint[]` 해제 시 `JNI_ABORT` 플래그 사용 — 네이티브에서 배열을 읽기만 하므로 writeback 불필요
- 배열 루프 내에서 title/body의 `LocalRef`를 명시적으로 삭제 — 대용량 일괄 색인 시 JNI 로컬 레퍼런스 테이블 고갈 방지
- `init { check(handle != 0L) }` — 초기화 실패를 즉시 예외로 노출하는 fail-fast 설계

## 모듈 구조

```
native/
├── src/main/
│   ├── cpp/
│   │   ├── CMakeLists.txt
│   │   ├── include/
│   │   │   ├── tokenizer.h
│   │   │   ├── inverted_index.h
│   │   │   └── search_engine.h
│   │   └── src/
│   │       ├── tokenizer.cpp        — UTF-8 디코더 + 바이그램/단어 토크나이저
│   │       ├── inverted_index.cpp   — TF-IDF 역색인, 바이너리 직렬화, 프리픽스 스캔
│   │       ├── search_engine.cpp    — 스레드 안전 래퍼 + 자동 영속화
│   │       └── fts_jni.cpp          — JNI 브릿지 (핸들 패턴, 예외 안전)
│   └── java/com/bossmg/android/fts/
│       ├── SearchEngine.kt          — Kotlin 인터페이스 (DI 친화적)
│       └── NativeSearchEngine.kt   — JNI 구현체 (Closeable)
└── build.gradle.kts
```

## 요구 사항

- Android minSdk **21** (Android 5.0 Lollipop)
- NDK **r25** 이상
- CMake **3.22.1** 이상
- C++17

## 참고 자료

- **[Introduction to Information Retrieval](https://nlp.stanford.edu/IR-book/)**  
  역색인(Inverted Index) 자료구조, TF-IDF 스코어링, 스무딩 IDF 변형(`log((N+1)/(df+1)) + 1`)의 이론적 기반

- **[RFC 3629 — UTF-8, a transformation format of ISO 10646](https://www.rfc-editor.org/rfc/rfc3629)**  
  수동 UTF-8 디코더(1~4바이트 시퀀스) 구현의 참조 명세

- **[Unicode Standard — Hangul Blocks](https://www.unicode.org/charts/)**  
  한글 유니코드 블록 범위 정의 (음절 U+AC00–U+D7A3, 자모, 호환 자모, 확장 블록)

- **[Android NDK — JNI Tips](https://developer.android.com/training/articles/perf-jni)**  
  포인터-핸들 패턴, LocalRef 관리, `JNI_ABORT` 플래그 등 JNI 구현 관용구

- **[cppreference — std::shared_mutex](https://en.cppreference.com/w/cpp/thread/shared_mutex)**  
  Readers-Writer Lock 구현 (C++17)

## 라이선스

```
Copyright 2026 Gyugle

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
