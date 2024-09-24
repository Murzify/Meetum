-dontoptimize
-ignorewarnings
-dontnote
-verbose

-dontwarn kotlinx.**

-keep class org.sqlite.** { *; }
-keep class sqlite.** { *; }

-keep class com.arkivanov.decompose.mainthread.** { *; }
-keep class * implements com.arkivanov.decompose.mainthread.MainThreadChecker { *; }
-keep class kotlinx.coroutines.swing.* { *; }
-keep class java.lang.management.* { *; }
-keep class com.google.firebase.firestore.FirestoreRegistrar { *; }
-keep class com.google.firebase.functions.FunctionsRegistrar { *; }
-keep class com.google.firebase.iid.Registrar { *; }
-keep class com.google.firebase.auth.FirebaseAuthRegistrar { *; }
-keep class com.google.firebase.installations.FirebaseInstallationsRegistrar { *; }
-keep class com.google.firebase.database.DatabaseRegistrar { *; }
-keep class io.ktor.client.engine.cio.CIOEngineContainer { *; }
-keep class io.ktor.serialization.kotlinx.json.KotlinxSerializationJsonExtensionProvider { *; }
