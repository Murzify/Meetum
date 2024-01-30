import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import java.io.FileInputStream
import java.util.*

plugins {
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.com.android.application)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.serialization)
    id(libs.plugins.multiplatform.resources.get().pluginId)
    id("com.github.gmazzo.buildconfig")
}

val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
keystoreProperties.load(FileInputStream(keystorePropertiesFile))

buildConfig {
    buildConfigField("PROJECT_ID", keystoreProperties["projectId"] as String )
    buildConfigField("APP_ID", keystoreProperties["applicationId"] as String )
    buildConfigField("API_KEY", keystoreProperties["apiKey"] as String )
}

sqldelight {
    databases {
        create("meetum-database") {
            packageName.set("com.murzify.meetum")
        }
    }
}

kotlin {
    androidTarget {
        apply(plugin = "com.google.gms.google-services")
        compilations.all {
            kotlinOptions {
                jvmTarget = "11"
            }
        }
    }
    
    jvm("desktop")

    sourceSets {

        val commonMain by getting {
            dependencies {
                // Compose
                implementation(compose.runtime)
                implementation(compose.foundation)
                @OptIn(ExperimentalComposeLibrary::class)
                implementation(compose.components.resources)

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

                // SQLDelight
                implementation(libs.sqldelight.coroutines)

                // moko resources
                implementation(libs.moko.resources)
                implementation(libs.moko.resources.compose)

                implementation(libs.uuid)
                implementation(libs.window.size)

                // Sentry
                implementation(libs.sentry.kmp)

                // Firebase
                implementation(libs.firebase.auth)

                implementation(libs.datastore.prefs)

                // Ktor
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.okhttp)
                implementation(libs.ktor.client.negotiation)
                implementation(libs.ktor.serialization.json)
                implementation(libs.ktor.logging)

                implementation(libs.napier)
                implementation(libs.kottie)

            }
        }
        val desktopMain by getting {
            dependsOn(commonMain)
            dependencies {
                implementation(compose.desktop.common)
                implementation(compose.desktop.currentOs)
                implementation(libs.coroutines.swing)
                implementation(libs.sqldelight.jvm)
                implementation(libs.ktor.logging.jvm)
            }
        }
        val androidMain by getting {
            dependsOn(commonMain)
            dependencies {
                implementation(libs.ui)
                implementation(libs.ui.tooling.preview)
                implementation(libs.activity.compose)
                implementation(libs.sqldelight.android)
                implementation(libs.lottie.compose)
                implementation(libs.calendar)
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
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    dependencies {
        debugImplementation(libs.ui.tooling)
    }
}

multiplatformResources {
    multiplatformResourcesPackage = "com.murzify.meetum"
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
