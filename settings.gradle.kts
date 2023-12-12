rootProject.name = "Meetum"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
pluginManagement {
//    includeBuild("build-logic")
    repositories {
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositories {
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        google()
        mavenCentral()
    }
}

//include(":app")
//include(":core:ui")
//include(":core:domain")
//include(":core:data")
//include(":core:database")
//include(":feature:calendar")
//include(":feature:services")
//include(":core:common")
include(":composeApp")
