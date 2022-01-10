plugins {
    id("com.projectronin.interop.gradle.junit")
}

dependencies {
    implementation("com.projectronin.interop:interop-common:${project.property("interopCommonVersion")}")
}
