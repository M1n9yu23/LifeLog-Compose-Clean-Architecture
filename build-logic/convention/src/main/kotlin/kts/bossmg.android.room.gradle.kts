import org.gradle.kotlin.dsl.dependencies

plugins {
    id("com.google.devtools.ksp")
}

dependencies {
    add("implementation", versionCatalog.findLibrary("androidx.room.runtime").get())
    add("ksp", versionCatalog.findLibrary("androidx.room.compiler").get())
}
