plugins {
    id("com.projectronin.interop.gradle.spring")
    id("com.projectronin.interop.gradle.ktorm")
    id("com.projectronin.interop.gradle.mockk")
}

dependencies {
    implementation("com.projectronin.interop:interop-common:${project.property("interopCommonVersion")}")
    implementation(project(":interop-queue"))

    // Spring
    implementation("org.springframework:spring-context")

    testImplementation("com.projectronin.interop:interop-common-test-db:${project.property("interopCommonVersion")}")
    testImplementation(project(":interop-queue-liquibase"))
}
