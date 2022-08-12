plugins {
    id("com.projectronin.interop.gradle.junit")
    id("com.projectronin.interop.gradle.spring")
}

dependencies {
    implementation(project(":interop-queue"))
    implementation(libs.interop.common)

    implementation(libs.ktorm.core)
    implementation(libs.ktorm.support.mysql)
    implementation("org.springframework:spring-context")

    testImplementation(project(":interop-queue-liquibase"))
    testImplementation(libs.interop.commonTestDb)

    testImplementation(libs.mockk)
    testImplementation(libs.rider.core)

    testRuntimeOnly(libs.bundles.test.mysql)
}

tasks.test {
    // Lets us mock the system time in unit tests
    // https://github.com/mockk/mockk/issues/681
    jvmArgs("--add-opens", "java.base/java.time=ALL-UNNAMED")
}
