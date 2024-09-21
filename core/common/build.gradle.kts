import com.murzify.meetum.buildlogic.convention.commonMainDependencies
import com.murzify.meetum.buildlogic.convention.desktopMainDependencies

plugins {
    alias(libs.plugins.serialization)
    id("core")
}

commonMainDependencies {
    implementation(libs.decompose)
    implementation(libs.decompose.extensions)
}

desktopMainDependencies {
    implementation(libs.coroutines.swing)
}