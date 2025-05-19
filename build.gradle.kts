plugins {
    id("org.springframework.boot") version "3.3.11"
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("jvm") version "1.9.23"
    kotlin("plugin.spring") version "1.9.23"
    kotlin("plugin.jpa") version "1.9.23"
    id("org.openapi.generator") version "7.4.0"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

kotlin {
    jvmToolchain(17)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("com.h2database:h2")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // JWT
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")

    // Swagger UI
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.4.0")

    // OpenAPI Generator
    implementation("org.openapitools:openapi-generator-gradle-plugin:7.4.0") {
        exclude(group = "org.slf4j", module = "slf4j-simple")
    }
    implementation("javax.validation:validation-api:2.0.1.Final")
    implementation("io.swagger.core.v3:swagger-annotations:2.2.20")
    implementation("javax.annotation:javax.annotation-api:1.3.2")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("jakarta.servlet:jakarta.servlet-api:6.0.0")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// OpenAPI Generator configuration
tasks.withType<org.openapitools.generator.gradle.plugin.tasks.GenerateTask> {
    generatorName.set("kotlin-spring")
    inputSpec.set("${rootDir}/src/main/resources/openapi.yml")
    outputDir.set("${buildDir}/generated")
    apiPackage.set("com.example.todo.api")
    modelPackage.set("com.example.todo.model")
    configOptions.set(mapOf(
        "dateLibrary" to "java8",
        "interfaceOnly" to "true",
        "useTags" to "true",
        "annotationLibrary" to "swagger2",
        "serviceInterface" to "false",
        "serviceImplementation" to "false",
        "reactive" to "false",
        "useSpringBoot3" to "true"
    ))
}

// Include generated sources in the main source set
sourceSets {
    main {
        java {
            srcDir("${buildDir}/generated/src/main/kotlin")
        }
    }
}

// Make sure the OpenAPI code is generated before compiling
tasks.compileKotlin {
    dependsOn(tasks.openApiGenerate)
}

// Task to copy openapi.yml to static directory when it changes
tasks.register<Copy>("copyOpenApiToStatic") {
    from("${rootDir}/src/main/resources/openapi.yml")
    into("${rootDir}/src/main/resources/static")
    // Only copy if the source file has changed
    inputs.file("${rootDir}/src/main/resources/openapi.yml")
    outputs.file("${rootDir}/src/main/resources/static/openapi.yml")
}

// Make sure the openapi.yml is copied to static directory after resources are processed
tasks.processResources {
    dependsOn("copyOpenApiToStatic")
}
