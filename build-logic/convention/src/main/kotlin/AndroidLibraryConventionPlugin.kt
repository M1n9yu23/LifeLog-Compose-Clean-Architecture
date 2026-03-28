import com.android.build.gradle.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.android")
            }

            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)
                defaultConfig.targetSdk = 36
            }
            configureSpotless()

            dependencies {
                add("implementation", versionCatalog.findLibrary("androidx.core.ktx").get())
                add("testImplementation", versionCatalog.findBundle("unit.test").get())
                add("androidTestImplementation", versionCatalog.findBundle("android.test").get())
            }
        }
    }
}
