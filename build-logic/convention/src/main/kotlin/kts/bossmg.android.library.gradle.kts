import com.android.build.gradle.LibraryExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

configure<LibraryExtension> {
    configureKotlinAndroid(this)
    defaultConfig.targetSdk = 36
}

dependencies {
    add("implementation", versionCatalog.findLibrary("androidx.core.ktx").get())
    add("testImplementation", versionCatalog.findBundle("unit.test").get())
    add("androidTestImplementation", versionCatalog.findBundle("android.test").get())
}
