plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":comparator-common"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")

    implementation("org.jetbrains.exposed:exposed-core:0.50.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.50.1")
    implementation("org.postgresql:postgresql:42.7.3")
    implementation("com.zaxxer:HikariCP:5.1.0")

    testImplementation(project(":comparator-repo-tests"))
    testImplementation(kotlin("test"))
    testImplementation("org.testcontainers:testcontainers:1.19.7")
    testImplementation("org.testcontainers:postgresql:1.19.7")
    testImplementation("org.testcontainers:junit-jupiter:1.19.7")
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}