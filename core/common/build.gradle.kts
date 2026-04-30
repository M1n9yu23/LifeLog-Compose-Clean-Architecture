plugins {
    alias(libs.plugins.bossmg.jvm.library)
}

dependencies {
    implementation(libs.coroutines.core)
    implementation(libs.hilt.core)
}
