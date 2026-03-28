import org.gradle.kotlin.dsl.dependencies

plugins {
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

dependencies {
    add("implementation", versionCatalog.findBundle("android.hilt").get())
    add("ksp", versionCatalog.findLibrary("hilt.compiler").get())
}
