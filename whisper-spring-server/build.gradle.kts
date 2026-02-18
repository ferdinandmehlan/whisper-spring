plugins {
    id("java")
    id("org.springframework.boot") version "4.0.0"
    id("com.avast.gradle.docker-compose") version "0.17.20"
}

tasks.bootRun {
    args("--spring.profiles.active=test")
}

java {
    toolchain {
        languageVersion.set(rootProject.extra["javaVersion"] as JavaLanguageVersion)
    }
}

dependencies {
    implementation(project(":whisper-spring"))
    implementation("org.springframework.boot:spring-boot-starter-web:4.0.0")
    implementation("org.springframework.boot:spring-boot-starter-webflux:4.0.0")
    implementation("org.springframework.boot:spring-boot-starter-validation:4.0.0")
    implementation("org.springframework.boot:spring-boot-starter-actuator:4.0.0")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.0")

    testImplementation(project(":whisper-spring-test-common"))
    testImplementation("org.springframework.boot:spring-boot-resttestclient:4.0.0")
    testImplementation("org.testcontainers:junit-jupiter:1.21.4")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

/*
 * Build
 */

tasks.register<Copy>("copyUIFiles") {
    group = "build"
    description = "Copy static UI files from the UI build"

    dependsOn(":whisper-spring-server-ui:compile")
    doFirst {
        delete("$projectDir/build/resources/main/static")
    }
    from("$rootDir/whisper-spring-server-ui/build")
    into("$projectDir/build/resources/main/static")
}

listOf("compileTestJava", "resolveMainClassName", "jar").forEach {
    tasks.named(it) { dependsOn("copyUIFiles") }
}

/*
 * Compile
 */

tasks.bootJar {
    manifest {
        attributes["Enable-Native-Access"] = "ALL-UNNAMED"
    }
}

/*
 * Check
 */

tasks.processTestResources {
    from("$rootDir/models") {
        include("ggml-tiny.bin")
    }
    dependsOn(":downloadTinyModel")
}

tasks.test {
    useJUnitPlatform()
    dependsOn("composeBuild")
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
