pluginManagement{
    val kotlinVersion: String by settings
    plugins{
        kotlin("jvm") version kotlinVersion
    }
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "StudyProject"

include("m1l1-first")
include(":comparator-api-v1")
include(":comparator-common")
include(":comparator-mappers-v1")
include("comparator-stubs")
include("comparator-app-ktor")
include("comparator-app-websocket")
include("comparator-stubs")
include("comparator-app-ktor")
include("comparator-app-websocket")
include("comparator-biz")
include("comparator-lib-cor")
include("comparator-repo-inmemory")
include("comparator-repo-postgres")
include("comparator-repo-tests")
