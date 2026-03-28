plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm")
}

configureKotlinJvm()

dependencies {
    add("testImplementation", versionCatalog.findBundle("unit.test").get())
}
