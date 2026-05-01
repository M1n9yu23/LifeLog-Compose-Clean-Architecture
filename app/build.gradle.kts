import java.util.Properties

plugins {
    alias(libs.plugins.bossmg.android.application)
    alias(libs.plugins.bossmg.android.hilt)
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.bossmg.android.lifelog"

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.bossmg.android.lifelog"
        versionCode = 1
        versionName = "1.0"
        resourceConfigurations += listOf("en", "ko")

        val props = Properties()
        rootProject.file("local.properties").takeIf { it.exists() }?.inputStream()?.use(props::load)
        buildConfigField("String", "WEB_CLIENT_ID", "\"${props["WEB_CLIENT_ID"] ?: ""}\"")
    }

    androidResources {
        generateLocaleConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
}

dependencies {
    implementation(project(":feature:home"))
    implementation(project(":feature:calendar"))
    implementation(project(":feature:settings"))
    implementation(project(":feature:mood"))
    implementation(project(":feature:memo"))
    implementation(project(":feature:photo"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:notifications"))
    implementation(project(":core:domain"))
    implementation(project(":core:data"))
    implementation(project(":core:auth"))
    implementation(project(":core:sync"))

    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.hilt.work)
    ksp(libs.androidx.hilt.compiler)
}
