plugins {
    alias(libs.plugins.interop.gradle.junit)
    alias(libs.plugins.interop.gradle.spring)
    alias(libs.plugins.interop.gradle.integration)
}

dependencies {
    implementation(project(":interop-queue"))
    implementation(libs.interop.common)
    implementation(libs.interop.kafka)
    implementation(libs.ronin.kafka)
    implementation(libs.event.interop.resource.internal)
    implementation("org.springframework:spring-context")
    implementation(libs.guava)

    testImplementation(libs.interop.commonTestDb) // necessary for integration test
    testImplementation(libs.mockk)
    testImplementation("org.springframework:spring-test")

    itImplementation(libs.interop.common)
    itImplementation(libs.interop.commonJackson)
    itImplementation(libs.interop.kafka)
    itImplementation(libs.testcontainers.mysql)
    itImplementation(libs.ronin.kafka)
    itImplementation(libs.event.interop.resource.internal)
    itImplementation(project(":interop-queue"))
}
