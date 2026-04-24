plugins {
    alias(libs.plugins.bossmg.android.library)
    alias(libs.plugins.bossmg.android.hilt)
    alias(libs.plugins.bossmg.android.room)
}

android {
    namespace = "com.bossmg.android.data"

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
    implementation(project(":core:domain"))
    implementation(libs.hanfts)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.appcompat)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.kotlinx.coroutines.test)
}
