pluginManagement {
    repositories {
        google()
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

rootProject.name = "Meetum"
include(":app")
include(":core:ui")
include(":core:domain")
include(":core:data")
include(":core:database")
include(":feature:calendar")
include(":feature:services")
