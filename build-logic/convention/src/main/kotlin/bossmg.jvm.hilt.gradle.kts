import org.gradle.kotlin.dsl.dependencies

plugins {
    id("com.google.devtools.ksp")
}

dependencies {
    add("implementation", libs.findLibrary("hilt.core").get())
    add("ksp", libs.findLibrary("hilt.compiler").get())
}
