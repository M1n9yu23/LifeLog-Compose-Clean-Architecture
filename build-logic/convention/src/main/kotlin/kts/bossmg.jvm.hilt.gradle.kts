import org.gradle.kotlin.dsl.dependencies

plugins {
    id("com.google.devtools.ksp")
}

dependencies {
    add("implementation", versionCatalog.findLibrary("hilt.core").get())
    add("ksp", versionCatalog.findLibrary("hilt.compiler").get())
}
