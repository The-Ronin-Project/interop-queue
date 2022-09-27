rootProject.name = "interop-queue-build"

include("interop-queue")
include("interop-queue-liquibase")
include("interop-queue-db")
include("interop-queue-monitor")

for (project in rootProject.children) {
    project.buildFileName = "${project.name}.gradle.kts"
}

pluginManagement {
    val interopGradleVersion = "2.0.0"
    plugins {
        id("com.projectronin.interop.gradle.base") version interopGradleVersion
        id("com.projectronin.interop.gradle.junit") version interopGradleVersion
        id("com.projectronin.interop.gradle.publish") version interopGradleVersion
        id("com.projectronin.interop.gradle.spring") version interopGradleVersion
        id("com.projectronin.interop.gradle.version") version interopGradleVersion
        id("org.springframework.boot") version "2.7.4"
    }

    repositories {
        maven {
            url = uri("https://repo.devops.projectronin.io/repository/maven-snapshots/")
            mavenContent {
                snapshotsOnly()
            }
        }
        maven {
            url = uri("https://repo.devops.projectronin.io/repository/maven-releases/")
            mavenContent {
                releasesOnly()
            }
        }
        maven {
            url = uri("https://repo.devops.projectronin.io/repository/maven-public/")
            mavenContent {
                releasesOnly()
            }
        }
        mavenLocal()
        gradlePluginPortal()
    }
}
