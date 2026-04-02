plugins {
    alias(libs.plugins.bossmg.android.feature)
    alias(libs.plugins.bossmg.android.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.bossmg.android.memo"
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:designsystem"))
    testImplementation(project(":core:testing"))

    implementation(libs.bundles.androidx.camera)
    implementation(libs.coil.compose)
}
