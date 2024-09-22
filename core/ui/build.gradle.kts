import com.murzify.meetum.buildlogic.convention.androidMainDependencies
import com.murzify.meetum.buildlogic.convention.commonMainDependencies

plugins {
    id("compose")
}

compose.resources {
    publicResClass = true
    packageOfResClass = "com.murzify.meetum.core.ui.resources"
    generateResClass = auto
}

commonMainDependencies { kmpExtension ->
    implementation(projects.core.domain)

    implementation(kmpExtension.compose.components.resources)
    implementation(libs.calendar)
    implementation(libs.window.size)
}
androidMainDependencies {
    implementation(libs.lottie.compose)
}