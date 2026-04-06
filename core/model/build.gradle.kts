plugins {
    alias(libs.plugins.bossmg.jvm.library)
    alias(libs.plugins.bossmg.jvm.hilt)
}

dependencies {
    implementation(project(":core:domain"))
}
