plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":comparator-lib-cor"))
    implementation(project(":comparator-common"))
    implementation(project(":comparator-stubs"))

    testImplementation(kotlin("test"))
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    testImplementation(project(":comparator-repo-tests"))
    testImplementation(project(":comparator-repo-inmemory"))
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}