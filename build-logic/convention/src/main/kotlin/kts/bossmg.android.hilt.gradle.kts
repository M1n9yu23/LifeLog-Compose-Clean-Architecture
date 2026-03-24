import org.gradle.kotlin.dsl.dependencies

plugins {
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

dependencies {
    add("implementation", libs.findBundle("android.hilt").get())
    add("ksp", libs.findLibrary("hilt.compiler").get())
}
