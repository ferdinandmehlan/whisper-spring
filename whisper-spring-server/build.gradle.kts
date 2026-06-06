plugins {
    id("java")
    id("org.springframework.boot") version "4.0.6"
    id("com.avast.gradle.docker-compose") version "0.17.20"
}

java {
    toolchain {
        languageVersion.set(rootProject.extra["javaVersion"] as JavaLanguageVersion)
    }
}

dependencies {
    implementation(project(":whisper-spring"))
    implementation(platform("org.springframework.boot:spring-boot-dependencies:4.0.6"))
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.0")

    testImplementation(project(":whisper-spring-test-common"))
    testImplementation("org.springframework.boot:spring-boot-starter-restclient")
    testImplementation("org.springframework.boot:spring-boot-resttestclient")
    testImplementation("org.testcontainers:junit-jupiter:1.21.4")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

/*
 * Compile
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

tasks.bootJar {
    manifest {
        attributes["Enable-Native-Access"] = "ALL-UNNAMED"
    }
}

tasks.bootRun {
    args("--spring.profiles.active=dev")
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

    if (project.hasProperty("cuda")) {
        useComposeFiles = listOf("../compose-cuda.yaml")
        environment.put("TAG", project.findProperty("TAG") ?: "dev-cuda")
    }
}

tasks.named("composeBuild") {
    dependsOn("bootJar")
}
