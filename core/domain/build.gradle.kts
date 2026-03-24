plugins {
    alias(libs.plugins.bossmg.jvm.library)
    alias(libs.plugins.bossmg.jvm.hilt)
}

dependencies {
    implementation(libs.coroutines.core)
}