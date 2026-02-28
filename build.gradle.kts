plugins {
    kotlin("jvm") version "2.0.20" apply false
}

group = "ru.levin.apps"
version = "1.0-SNAPSHOT"

allprojects {
    repositories {
        mavenCentral()
    }
}

subprojects {
    group = rootProject.group
    version = rootProject.version
}