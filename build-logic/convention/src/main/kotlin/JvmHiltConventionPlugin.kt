import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class JvmHiltConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply {
                apply("com.google.devtools.ksp")
            }

            dependencies {
                add("implementation", versionCatalog.findLibrary("hilt.core").get())
                add("ksp", versionCatalog.findLibrary("hilt.compiler").get())
            }
        }
    }
}
