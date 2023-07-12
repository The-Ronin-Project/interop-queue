plugins {
    alias(libs.plugins.interop.gradle.junit)
    alias(libs.plugins.interop.gradle.spring.boot)
}

dependencies {
    implementation(project(":interop-queue"))
    implementation(project(":interop-queue-db"))
    implementation(platform(libs.spring.boot.parent)) {
        exclude(group = "org.jetbrains.kotlinx")
    }
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation(libs.ktorm.core)
    implementation(libs.dog.stats.d)

    testImplementation(libs.mockk)
}
