plugins {
    id("com.projectronin.interop.gradle.junit")
}

dependencies {
    api(libs.event.interop.resource.internal)
    implementation(libs.interop.common)
}
