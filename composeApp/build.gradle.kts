import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import java.io.FileInputStream
import java.util.*

plugins {
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.com.android.application)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.serialization)
    alias(libs.plugins.compose.compiler)
    id("com.github.gmazzo.buildconfig")
}

val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
keystoreProperties.load(FileInputStream(keystorePropertiesFile))

buildConfig {
    packageName("com.murzify.meetum.kmp")
    buildConfigField("PROJECT_ID", keystoreProperties["projectId"] as String )
    buildConfigField("APP_ID", keystoreProperties["applicationId"] as String )
    buildConfigField("API_KEY", keystoreProperties["apiKey"] as String )
    buildConfigField("DB_URL", keystoreProperties["databaseUrl"] as String )
}

kotlin {
    androidTarget {
        apply(plugin = "com.google.gms.google-services")
    }

    jvm("desktop")

    sourceSets {

        val commonMain by getting {
            dependencies {
                implementation(projects.core.domain)
                implementation(projects.core.network)
                implementation(projects.core.common)
                implementation(projects.core.data)
                implementation(projects.core.database)
                implementation(projects.core.datastore)
                implementation(projects.core.ui)

                implementation(projects.feature.auth)
                implementation(projects.feature.services)
                implementation(projects.feature.calendar)

                // Compose
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.components.resources)
                implementation(libs.calendar)

                // Material design
                implementation(compose.material)
                implementation(compose.material3)

                // Kotlin libs
                implementation(libs.kotlinx.datetime)
                implementation(libs.kotlinx.serialization)
                implementation(libs.coroutines)

                // Decompose
                implementation(libs.decompose)
                implementation(libs.decompose.extensions)

                // Koin
                implementation(libs.koin.core)

                implementation(libs.uuid)
                implementation(libs.window.size)

                // Sentry
                implementation(libs.sentry.kmp)

                // Firebase
                implementation(libs.firebase.auth)

                implementation(libs.datastore.prefs)

                implementation(libs.napier)
                implementation(libs.kottie)

            }
        }
        val desktopMain by getting {
            dependsOn(commonMain)
            dependencies {
                implementation(compose.desktop.common)
                implementation(compose.desktop.currentOs)
            }
        }
        val androidMain by getting {
            dependsOn(commonMain)
            dependencies {
                implementation(libs.ui)
                implementation(libs.ui.tooling.preview)
                implementation(libs.activity.compose)
                implementation(libs.lottie.compose)
                implementation(libs.play.services.auth)
            }
        }
    }
}

android {
    namespace = "com.murzify.meetum"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        applicationId = "com.murzify.meetum"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    signingConfigs {
        create("release") {
            storeFile = file(keystoreProperties["store"] as String)
            storePassword = keystoreProperties["keyStorePassword"] as String
            keyPassword = keystoreProperties["keyStorePassword"] as String
            keyAlias = keystoreProperties["alias"] as String
        }
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles("rules-android.pro")
            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    dependencies {
        debugImplementation(libs.ui.tooling)
    }
}
dependencies {
    implementation(libs.google.firebase.database)
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            packageVersion = "1.0.0"
            copyright = "© 2023 Murzify. All rights reserved."
            vendor = "Murzify"
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb, TargetFormat.Exe)
            linux {
                packageName = "meetum"
                shortcut = true
                iconFile = project.file("src/commonMain/resources/drawable/ic_launcher.png")
            }
            macOS {
                packageName = "meetum"
                iconFile = project.file("src/commonMain/resources/drawable/ic_launcher.icns")
            }
            windows {
                packageName = "Meetum"
                shortcut = true
                iconFile = project.file("src/commonMain/resources/drawable/ic_launcher.ico")
            }
            modules("java.instrument", "java.prefs", "java.sql", "jdk.unsupported","jdk.crypto.ec","jdk.localedata")
        }
        buildTypes.release.proguard {
            configurationFiles.from("rules.pro")
        }
    }
}
