plugins {
    alias(libs.plugins.bossmg.android.feature)
    alias(libs.plugins.bossmg.android.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.bossmg.android.mood"
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:designsystem"))
    testImplementation(project(":core:testing"))

    implementation(libs.coil.compose)
}
