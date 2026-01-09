plugins {
    id("java")
    id("org.springframework.boot") version "4.0.0"
    id("com.avast.gradle.docker-compose") version "0.17.20"
}

java {
    toolchain {
        languageVersion.set(rootProject.extra["javaVersion"] as JavaLanguageVersion)
    }
}

dependencies {
    implementation(project(":whisper-spring"))
    implementation("org.springframework.boot:spring-boot-starter-web:4.0.0")
    implementation("org.springframework.boot:spring-boot-starter-validation:4.0.0")
    implementation("org.springframework.boot:spring-boot-starter-actuator:4.0.0")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.0")

    testImplementation(project(":whisper-spring-test-common"))
    testImplementation("org.springframework.boot:spring-boot-resttestclient:4.0.0")
    testImplementation("org.testcontainers:junit-jupiter:1.21.4")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

/*
 * Check
 */

tasks.register<Copy>("copyTestModel") {
    from("$rootDir/models/ggml-tiny.bin")
    into("src/test/resources/test-models/")
    dependsOn(":downloadTinyModel")
    onlyIf { !file("src/test/resources/test-models/ggml-tiny.bin").exists() }
}

tasks.test {
    useJUnitPlatform()
    dependsOn("copyTestModel")
}

/*
 * Compose
 */

dockerCompose {
    setProjectName("whisper-spring")
    useComposeFiles = listOf("../compose.yaml")
    environment.put("TAG", project.findProperty("TAG") ?: "dev")
}

tasks.named("composeBuild") {
    dependsOn("bootJar")
}
