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
