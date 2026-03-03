plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":comparator-common"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")

    testImplementation(project(":comparator-repo-tests"))
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}