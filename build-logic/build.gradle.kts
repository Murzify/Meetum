plugins {
    id("java-library")
    `kotlin-dsl`
}

group = "com.murzify.meetum.build_logic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

gradlePlugin {
    plugins {
        register("feature") {
            id = "meetum.feature"
            implementationClass = "com.murzify.meetum.build_logic.FeaturePlugin"
        }
        register("hilt") {
            id = "meetum.hilt"
            implementationClass = "com.murzify.meetum.build_logic.HiltPlugin"
        }
        register("unitTests") {
            id = "meetum.unitTests"
            implementationClass = "com.murzify.meetum.build_logic.UnitTestPlugin"
        }
    }
}