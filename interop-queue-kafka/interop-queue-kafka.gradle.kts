plugins {
    id("com.projectronin.interop.gradle.junit")
    id("com.projectronin.interop.gradle.spring")
    id("com.projectronin.interop.gradle.integration")
}

dependencies {
    implementation(project(":interop-queue"))
    implementation(libs.interop.common)
    implementation(libs.interop.kafka)
    implementation(libs.ronin.kafka)
    implementation(libs.ronin.kafka.event.resource)
    implementation("org.springframework:spring-context")

    testImplementation(libs.interop.commonTestDb) // necessary for integration test
    testImplementation(libs.mockk)
    testImplementation("org.springframework:spring-test")
}
