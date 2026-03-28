import com.android.build.gradle.LibraryExtension

plugins {
    id("bossmg.android.library")
    id("org.jetbrains.kotlin.plugin.compose")
}

configure<LibraryExtension> {
    configureAndroidCompose(this)
}

dependencies {
    add("implementation", versionCatalog.findBundle("compose.navigation").get())
    add("implementation", versionCatalog.findBundle("compose.viewmodel").get())
}
