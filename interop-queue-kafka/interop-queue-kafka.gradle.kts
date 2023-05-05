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
