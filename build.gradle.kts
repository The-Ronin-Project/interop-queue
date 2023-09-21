plugins {
    alias(libs.plugins.interop.gradle.junit) apply false
    alias(libs.plugins.interop.gradle.publish) apply false
    alias(libs.plugins.interop.gradle.spring) apply false
    alias(libs.plugins.interop.gradle.integration)
    alias(libs.plugins.interop.gradle.version)
    alias(libs.plugins.interop.version.catalog)
}

subprojects {
    apply(plugin = "com.projectronin.interop.gradle.publish")

    // Disable releases hub from running on the subprojects. Main project will handle it all.
    tasks.filter { it.group.equals("releases hub", ignoreCase = true) }.forEach { it.enabled = false }
}
