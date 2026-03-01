pluginManagement{
    val kotlinVersion: String by settings
    plugins{
        kotlin("jvm") version kotlinVersion
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "StudyProject"

includeBuild("lessons")
includeBuild("my-project-module")
includeBuild("build-plugin")
