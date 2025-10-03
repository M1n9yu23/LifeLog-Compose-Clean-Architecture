pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "LifeLog"
include(":app")
include(":feature:home")
include(":core:data")
include(":core:domain")
include(":core:designsystem")
include(":core:testing")
include(":feature:calendar")
include(":feature:mood")
include(":feature:photo")
include(":feature:memo")
