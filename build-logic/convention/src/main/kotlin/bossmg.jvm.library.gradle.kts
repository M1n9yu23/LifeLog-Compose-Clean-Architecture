plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm")
}

configureKotlinJvm()

dependencies {
    add("testImplementation", libs.findBundle("unit.test").get())
}
