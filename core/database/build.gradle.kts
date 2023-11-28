@Suppress("DSL_SCOPE_VIOLATION") 
plugins {
    alias(libs.plugins.com.android.library)
    alias(libs.plugins.org.jetbrains.kotlin.android)
    alias(libs.plugins.sqldelight)
    id("meetum.koin")
    id("meetum.unitTests")
    id("meetum.instrumentalTest")
}

sqldelight {
    databases {
        create("meetum-database") {
            packageName.set("com.murzify.meetum")
        }
    }
}

android {
    namespace = "com.murzify.meetum.core.database"
    compileSdk = 34

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    dependencies {
        implementation(libs.sqldelight.android)
    }
}

dependencies {
    implementation(project(":core:domain"))
    implementation(libs.kotlinx.datetime)
    implementation(libs.sqldelight.coroutines)
    implementation(libs.core.ktx)
    implementation(libs.uuid)
}