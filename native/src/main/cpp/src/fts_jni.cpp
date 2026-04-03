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
#include "search_engine.h"

#include <jni.h>
#include <android/log.h>
#include <string>
#include <vector>

#define LOG_TAG "LifeLogFTS"
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

static std::string jstringToStdString(JNIEnv *env, jstring js) {
    if (!js) return "";
    const char *chars = env->GetStringUTFChars(js, nullptr);
    std::string result(chars);
    env->ReleaseStringUTFChars(js, chars);
    return result;
}

static fts::SearchEngine *toEngine(jlong handle) {
    return reinterpret_cast<fts::SearchEngine *>(handle);
}

extern "C" {

JNIEXPORT jlong JNICALL
Java_com_bossmg_android_fts_NativeSearchEngine_nativeCreate(
        JNIEnv *env, jobject, jstring index_path) {
    try {
        auto path = jstringToStdString(env, index_path);
        auto *engine = new fts::SearchEngine(path);
        return reinterpret_cast<jlong>(engine);
    } catch (...) {
        LOGE("nativeCreate: unexpected exception");
        return 0L;
    }
}

JNIEXPORT void JNICALL
Java_com_bossmg_android_fts_NativeSearchEngine_nativeDestroy(
        JNIEnv *, jobject, jlong handle) {
    delete toEngine(handle);
}

JNIEXPORT void JNICALL
Java_com_bossmg_android_fts_NativeSearchEngine_nativeIndexDocument(
        JNIEnv *env, jobject, jlong handle,
        jint doc_id, jstring title, jstring body) {
    auto *engine = toEngine(handle);
    if (!engine) return;
    try {
        engine->indexDocument(
                static_cast<int>(doc_id),
                jstringToStdString(env, title),
                jstringToStdString(env, body));
    } catch (...) {
        LOGE("nativeIndexDocument: unexpected exception");
    }
}

JNIEXPORT void JNICALL
Java_com_bossmg_android_fts_NativeSearchEngine_nativeRemoveDocument(
        JNIEnv *, jobject, jlong handle, jint doc_id) {
    auto *engine = toEngine(handle);
    if (!engine) return;
    try {
        engine->removeDocument(static_cast<int>(doc_id));
    } catch (...) {
        LOGE("nativeRemoveDocument: unexpected exception");
    }
}

JNIEXPORT jintArray JNICALL
Java_com_bossmg_android_fts_NativeSearchEngine_nativeSearch(
        JNIEnv *env, jobject, jlong handle,
        jstring query, jint limit) {
    auto *engine = toEngine(handle);
    if (!engine) return env->NewIntArray(0);
    try {
        auto ids = engine->search(
                jstringToStdString(env, query),
                static_cast<int>(limit));

        jintArray result = env->NewIntArray(static_cast<jsize>(ids.size()));
        if (!result) return env->NewIntArray(0);
        env->SetIntArrayRegion(result, 0,
                               static_cast<jsize>(ids.size()),
                               reinterpret_cast<const jint *>(ids.data()));
        return result;
    } catch (...) {
        LOGE("nativeSearch: unexpected exception");
        return env->NewIntArray(0);
    }
}

JNIEXPORT void JNICALL
Java_com_bossmg_android_fts_NativeSearchEngine_nativeRebuildIndex(
        JNIEnv *env, jobject, jlong handle,
        jintArray ids, jobjectArray titles, jobjectArray bodies) {
    auto *engine = toEngine(handle);
    if (!engine) return;
    try {
        jsize len = env->GetArrayLength(ids);
        jint *id_arr = env->GetIntArrayElements(ids, nullptr);
        if (!id_arr) {
            LOGE("nativeRebuildIndex: GetIntArrayElements returned null");
            return;
        }

        std::vector<std::tuple<int, std::string, std::string>> docs;
        docs.reserve(static_cast<size_t>(len));

        for (jsize i = 0; i < len; ++i) {
            auto title_js = static_cast<jstring>(env->GetObjectArrayElement(titles, i));
            auto body_js = static_cast<jstring>(env->GetObjectArrayElement(bodies, i));
            docs.emplace_back(
                    static_cast<int>(id_arr[i]),
                    jstringToStdString(env, title_js),
                    jstringToStdString(env, body_js));
            env->DeleteLocalRef(title_js);
            env->DeleteLocalRef(body_js);
        }

        env->ReleaseIntArrayElements(ids, id_arr, JNI_ABORT);
        engine->rebuildIndex(docs);
    } catch (...) {
        LOGE("nativeRebuildIndex: unexpected exception");
    }
}

}
