plugins {
    alias(libs.plugins.bossmg.android.feature)
}

android {
    namespace = "com.bossmg.android.designsystem"
}

dependencies {
    implementation(project(":core:domain"))

    implementation(libs.coil.compose)
    implementation(libs.androidx.compose.material.icons.extended)
}