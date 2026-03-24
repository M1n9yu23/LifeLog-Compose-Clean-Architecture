import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import com.diffplug.gradle.spotless.SpotlessExtension

internal fun Project.configureSpotless() {
    with(pluginManager) {
        apply("com.diffplug.spotless")
    }

    extensions.configure<SpotlessExtension> {
        kotlin {
            target("**/*.kt")
            targetExclude("**/build/**/*.kt")
            ktlint("1.8.0")
        }
        kotlinGradle {
            target("**/*.gradle.kts")
            targetExclude("**/build/**/*.gradle.kts")
            ktlint("1.8.0")
        }
    }
}
