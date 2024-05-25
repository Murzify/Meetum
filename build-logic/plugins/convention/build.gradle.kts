plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(libs.android.gradle)
    implementation(libs.kotlin.gradle)

    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}
