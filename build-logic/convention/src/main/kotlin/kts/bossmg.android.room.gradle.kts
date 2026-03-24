import org.gradle.kotlin.dsl.dependencies

plugins {
    id("com.google.devtools.ksp")
}

dependencies {
    add("implementation", libs.findLibrary("androidx.room.runtime").get())
    add("ksp", libs.findLibrary("androidx.room.compiler").get())
}
