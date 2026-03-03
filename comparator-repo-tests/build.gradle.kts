plugins {
    kotlin("jvm")
}

dependencies {
    api(project(":comparator-common"))
    api("org.jetbrains.kotlin:kotlin-test-junit5:2.0.20")
    api("org.junit.jupiter:junit-jupiter-api:5.10.2")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
}

kotlin {
    jvmToolchain(21)
}