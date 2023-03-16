rootProject.name = "interop-queue-build"

include("interop-queue")
include("interop-queue-liquibase")
include("interop-queue-db")
include("interop-queue-kafka")
include("interop-queue-monitor")

for (project in rootProject.children) {
    project.buildFileName = "${project.name}.gradle.kts"
}

pluginManagement {
    plugins {
        id("com.projectronin.interop.gradle.base") version "3.0.0"
        id("com.projectronin.interop.gradle.junit") version "3.0.0"
        id("com.projectronin.interop.gradle.publish") version "3.0.0"
        id("com.projectronin.interop.gradle.spring") version "3.0.0"
        id("com.projectronin.interop.gradle.spring-boot") version "3.0.0"
        id("com.projectronin.interop.gradle.version") version "3.0.0"
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
