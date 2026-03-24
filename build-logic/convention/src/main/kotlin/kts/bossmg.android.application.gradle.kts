import com.android.build.api.dsl.ApplicationExtension

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

configure<ApplicationExtension> {
    configureKotlinAndroid(this)
    configureAndroidCompose(this)
    defaultConfig.targetSdk = 36
}
