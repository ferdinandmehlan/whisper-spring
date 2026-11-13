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
    implementation(project(":core"))
    implementation(platform(libs.springBootDependencies))
    implementation(libs.springBootStarterWeb)
    implementation(libs.springBootStarterWebflux)
    implementation(libs.springBootStarterWebsocket)
    implementation(libs.springBootStarterValidation)
    implementation(libs.springBootStarterActuator)
    implementation(libs.springdoc)

    testImplementation(project(":test-common"))
    testImplementation(libs.springBootStarterRestclient)
    testImplementation(libs.springBootResttestclient)
    testImplementation(libs.testcontainers)

    testRuntimeOnly(libs.junitPlatformLauncher)
}

/*
 * Compile
 */

tasks.processResources {
    dependsOn(":buildNativeLibraries", ":downloadTinyModel")
}

tasks.register<Copy>("copyUIFiles") {
    group = "build"
    description = "Copy static UI files from the UI build"

    dependsOn(":server-ui:compile")
    doFirst {
        delete("$projectDir/build/resources/main/static")
    }
    from("$rootDir/server-ui/build") {
        // Spotless keeps its intermediate results in the same build directory.
        exclude("spotless-clean/**", "spotless-lints/**")
    }
    into("$projectDir/build/resources/main/static")
}

listOf("compileTestJava", "resolveMainClassName").forEach {
    tasks.named(it) { dependsOn("copyUIFiles") }
}

tasks.jar { enabled = false }

tasks.bootJar {
    archiveBaseName.set("whisper-spring-server")
    manifest {
        attributes["Enable-Native-Access"] = "ALL-UNNAMED"
    }
}

tasks.bootRun {
    args("--spring.profiles.active=dev")
    jvmArgs("--enable-native-access=ALL-UNNAMED")
}

/*
 * Check
 */

tasks.test {
    useJUnitPlatform()
    jvmArgs("--enable-native-access=ALL-UNNAMED")
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
