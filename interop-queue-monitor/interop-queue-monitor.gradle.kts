plugins {
    id("com.projectronin.interop.gradle.junit")
    id("com.projectronin.interop.gradle.spring")
    id("org.springframework.boot")
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
