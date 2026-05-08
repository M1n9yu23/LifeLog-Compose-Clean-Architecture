plugins {
    alias(libs.plugins.bossmg.android.library)
    alias(libs.plugins.bossmg.android.hilt)
}

android {
    namespace = "com.bossmg.android.testing"
}

dependencies {
    implementation(project(":core:data"))
    implementation(project(":core:domain"))
    implementation(project(":notifications"))

    implementation(libs.bundles.room)
    implementation(libs.androidx.runner)
    implementation(libs.hilt.android.testing)
    implementation(libs.mockk)
    implementation(libs.kotlinx.coroutines.test)
}
