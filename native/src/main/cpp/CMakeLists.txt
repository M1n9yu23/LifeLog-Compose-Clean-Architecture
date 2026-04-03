cmake_minimum_required(VERSION 3.22.1)
project("lifelog_fts" LANGUAGES CXX)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

add_library(
    lifelog_fts
    SHARED
    src/tokenizer.cpp
    src/inverted_index.cpp
    src/search_engine.cpp
    src/fts_jni.cpp
)

target_include_directories(lifelog_fts PRIVATE include)

target_compile_options(lifelog_fts PRIVATE
    -O2
    -fvisibility=hidden
    -Wall
)

find_library(log-lib log)
target_link_libraries(lifelog_fts ${log-lib})

target_link_options(lifelog_fts PRIVATE "-Wl,-z,max-page-size=16384")
