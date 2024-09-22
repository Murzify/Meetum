import com.murzify.meetum.buildlogic.convention.androidMainDependencies
import com.murzify.meetum.buildlogic.convention.commonMainDependencies
import com.murzify.meetum.buildlogic.convention.desktopMainDependencies

plugins {
    alias(libs.plugins.sqldelight)
    id("core")
}

sqldelight {
    databases {
        create("meetum-database") {
            packageName.set("com.murzify.meetum")
        }
    }
}

commonMainDependencies {
    implementation(projects.core.domain)
    implementation(projects.core.common)

    implementation(libs.uuid)
    implementation(libs.sqldelight.coroutines)
}

androidMainDependencies {
    implementation(libs.sqldelight.android)
}

desktopMainDependencies {
    implementation(libs.sqldelight.jvm)
}

