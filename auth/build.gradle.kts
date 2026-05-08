plugins {
    alias(libs.plugins.bossmg.android.library)
    alias(libs.plugins.bossmg.android.hilt)
    alias(libs.plugins.bossmg.android.firebase)
}

android {
    namespace = "com.bossmg.android.auth"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:domain"))
    implementation(libs.firebase.auth)
    implementation(libs.credentials)
    implementation(libs.credentials.play.services.auth)
    implementation(libs.googleid)
}
