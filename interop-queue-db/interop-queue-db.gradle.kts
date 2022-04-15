plugins {
    id("com.projectronin.interop.gradle.spring")
    id("com.projectronin.interop.gradle.ktorm")
    id("com.projectronin.interop.gradle.mockk")
}

dependencies {
    implementation(libs.interop.common)
    implementation(project(":interop-queue"))

    // Spring
    implementation("org.springframework:spring-context")

    testImplementation(libs.interop.commonTestDb)
    testImplementation(project(":interop-queue-liquibase"))
}
