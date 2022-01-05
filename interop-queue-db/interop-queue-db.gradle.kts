plugins {
    id("com.projectronin.interop.gradle.spring")
    id("com.projectronin.interop.gradle.ktorm")
    id("com.projectronin.interop.gradle.mockk")
}

dependencies {
    implementation("com.projectronin.interop:interop-common")
    implementation(project(":interop-queue"))

    // Spring
    implementation("org.springframework:spring-context")

    implementation("com.projectronin.interop:interop-common-test-db")
    testImplementation(project(":interop-queue-liquibase"))
}
