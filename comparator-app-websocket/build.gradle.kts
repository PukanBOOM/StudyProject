val ktorVersion = "2.3.12"

plugins {
    kotlin("jvm")
    id("io.ktor.plugin") version "2.3.12"
}

application {
    mainClass.set("ru.levin.apps.comparator.app.websocket.ApplicationKt")
}

dependencies {
    implementation(project(":comparator-api-v1"))
    implementation(project(":comparator-common"))
    implementation(project(":comparator-mappers-v1"))
    implementation(project(":comparator-biz"))
    implementation(project(":comparator-repo-inmemory"))
    implementation(project(":comparator-repo-postgres"))

    implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-netty-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-websockets-jvm:$ktorVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.17.0")

    testImplementation(kotlin("test"))
    testImplementation("io.ktor:ktor-server-tests-jvm:$ktorVersion")
    testImplementation("io.ktor:ktor-client-websockets-jvm:$ktorVersion")
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}

ktor {
    docker {
        jreVersion.set(JavaVersion.VERSION_21)
        localImageName.set("comparator-app-websocket")
        imageTag.set("latest")
    }
}