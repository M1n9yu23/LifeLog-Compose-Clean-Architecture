plugins {
    alias(libs.plugins.bossmg.android.application)
    alias(libs.plugins.bossmg.android.hilt)
}

android {
    namespace = "com.bossmg.android.lifelog"

    defaultConfig {
        applicationId = "com.bossmg.android.lifelog"
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

dependencies {
    implementation(project(":feature:home"))
    implementation(project(":feature:calendar"))
    implementation(project(":feature:mood"))
    implementation(project(":feature:memo"))
    implementation(project(":feature:photo"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:notifications"))
    implementation(project(":core:domain"))
    implementation(project(":core:data"))

    implementation(libs.androidx.lifecycle.runtime.ktx)
}