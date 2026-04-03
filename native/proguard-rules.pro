# Keep JNI bridge class and all its members to prevent R8 from removing
# or obfuscating methods declared as 'external' in Kotlin.
-keep class com.bossmg.android.fts.NativeSearchEngine {
    *;
}
