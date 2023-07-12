plugins {
    alias(libs.plugins.interop.gradle.junit)
}

dependencies {
    api(libs.event.interop.resource.internal)
    implementation(libs.interop.common)
}
