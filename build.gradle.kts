// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.jetbrains.kotlin.jvm) apply false
    alias(libs.plugins.hilt.android) apply false
    alias(libs.plugins.kotlin.ksp) apply false
    alias(libs.plugins.spotless)
}

spotless {
    kotlin {
        target("**/*.kt")
        targetExclude("**/build/**/*.kt", "**/bin/**/*.kt", "spotless/**")
        ktlint("1.8.0")
        licenseHeaderFile(rootProject.file("spotless/copyright.kt"))
    }
    kotlinGradle {
        target("**/*.gradle.kts")
        targetExclude("**/build/**/*.gradle.kts", "**/bin/**/*.gradle.kts", "spotless/**")
        ktlint("1.8.0")
    }
    format("cpp") {
        target("native/src/**/*.cpp", "native/src/**/*.h")
        licenseHeaderFile(rootProject.file("spotless/copyright.cpp"), "(#pragma|#include|namespace)")
    }
}
