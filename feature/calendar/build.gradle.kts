plugins {
    alias(libs.plugins.bossmg.android.feature)
    alias(libs.plugins.bossmg.android.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.bossmg.android.calendar"
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:model"))
    testImplementation(project(":core:testing"))

    implementation(libs.kotlinx.collections.immutable)
    implementation(libs.coil.compose)
}
