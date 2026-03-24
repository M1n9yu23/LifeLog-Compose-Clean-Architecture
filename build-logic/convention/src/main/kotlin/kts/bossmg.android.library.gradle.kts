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
    add("implementation", libs.findLibrary("androidx.core.ktx").get())
    add("testImplementation", libs.findBundle("unit.test").get())
    add("androidTestImplementation", libs.findBundle("android.test").get())
}
