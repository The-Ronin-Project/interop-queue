plugins {
    id("com.projectronin.interop.gradle.base")
    id("com.projectronin.interop.gradle.junit") apply false
    id("com.projectronin.interop.gradle.ktorm") apply false
    id("com.projectronin.interop.gradle.mockk") apply false
    id("com.projectronin.interop.gradle.publish") apply false
    id("com.projectronin.interop.gradle.spring") apply false
}

subprojects {
    apply(plugin = "com.projectronin.interop.gradle.publish")
}
