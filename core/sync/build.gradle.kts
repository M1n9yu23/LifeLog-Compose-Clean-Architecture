plugins {
    alias(libs.plugins.bossmg.android.library)
    alias(libs.plugins.bossmg.android.hilt)
    alias(libs.plugins.bossmg.android.firebase)
}

android {
    namespace = "com.bossmg.android.sync"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:domain"))
    implementation(project(":core:data"))
    implementation(project(":core:auth"))
    implementation(libs.firebase.firestore)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.hilt.work)
    ksp(libs.androidx.hilt.compiler)
}
