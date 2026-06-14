plugins {
    id("java")
    alias(libs.plugins.springBoot)
    alias(libs.plugins.dockerCompose)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.java.get()))
    }
}

dependencies {
    implementation(project(":whisper-spring"))
    implementation(platform(libs.springBootDependencies))
    implementation(libs.springBootStarterWeb)
    implementation(libs.springBootStarterWebflux)
    implementation(libs.springBootStarterWebsocket)
    implementation(libs.springBootStarterValidation)
    implementation(libs.springBootStarterActuator)
    implementation(libs.springdoc)

    testImplementation(project(":whisper-spring-test-common"))
    testImplementation(libs.springBootStarterRestclient)
    testImplementation(libs.springBootResttestclient)
    testImplementation(libs.testcontainers)

    testRuntimeOnly(libs.junitPlatformLauncher)
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
