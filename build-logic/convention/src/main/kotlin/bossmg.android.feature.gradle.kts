import com.android.build.gradle.LibraryExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

plugins {
    id("bossmg.android.library")
    id("org.jetbrains.kotlin.plugin.compose")
}

configure<LibraryExtension> {
    configureAndroidCompose(this)
}

dependencies {
    add("implementation", libs.findBundle("compose.navigation").get())
    add("implementation", libs.findBundle("compose.viewmodel").get())
}
