plugins {
    kotlin("jvm")
    id("org.openapi.generator") version "7.4.0"
}

dependencies {
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.17.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.0")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.17.0")

    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(21)
}

openApiGenerate {
    generatorName.set("kotlin")
    inputSpec.set("$projectDir/src/main/resources/spec-comparator-v1.yaml")
    outputDir.set("$buildDir/generate-resources")
    packageName.set("ru.levin.apps.comparator.api.v1")
    modelPackage.set("ru.levin.apps.comparator.api.v1.models")
    globalProperties.set(
        mapOf(
            "models" to "",
            "modelDocs" to "false",
            "modelTests" to "false",
        )
    )
    configOptions.set(
        mapOf(
            "dateLibrary" to "string",
            "enumPropertyNaming" to "UPPERCASE",
            "collectionType" to "list",
            "serializationLibrary" to "jackson",
            "sourceFolder" to "src/main/kotlin",
        )
    )
}

sourceSets {
    main {
        kotlin.srcDir("$buildDir/generate-resources/src/main/kotlin")
    }
}

tasks {
    compileKotlin {
        dependsOn(openApiGenerate)
    }
    test {
        useJUnitPlatform()
    }
}