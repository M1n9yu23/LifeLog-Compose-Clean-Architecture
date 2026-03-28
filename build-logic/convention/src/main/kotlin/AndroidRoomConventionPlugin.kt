import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidRoomConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply {
                apply("com.google.devtools.ksp")
            }

            dependencies {
                add("implementation", versionCatalog.findLibrary("androidx.room.runtime").get())
                add("ksp", versionCatalog.findLibrary("androidx.room.compiler").get())
            }
        }
    }
}
