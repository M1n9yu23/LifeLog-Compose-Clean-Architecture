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
        targetExclude("**/build/**/*.kt", "**/bin/**/*.kt")
        ktlint("1.8.0")
        licenseHeaderFile(rootProject.file("spotless/copyright.kt"))
    }
    kotlinGradle {
        target("**/*.gradle.kts")
        targetExclude("**/build/**/*.gradle.kts", "**/bin/**/*.gradle.kts")
        ktlint("1.8.0")
        licenseHeaderFile(rootProject.file("spotless/copyright.kt"), "(^(?!.*\\/\\/).*$)")
    }
    format("cpp") {
        target("**/*.cpp", "**/*.h")
        targetExclude("**/build/**")
        licenseHeaderFile(rootProject.file("spotless/copyright.cpp"), "(#pragma|#include|namespace)")
    }
}
