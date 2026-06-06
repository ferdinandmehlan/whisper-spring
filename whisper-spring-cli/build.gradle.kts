plugins {
    id("java")
    alias(libs.plugins.springBoot)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.java.get()))
    }
}

dependencies {
    implementation(project(":whisper-spring"))
    implementation(libs.picocli)

    annotationProcessor(libs.picocliCodegen)

    testImplementation(project(":whisper-spring-test-common"))

    testRuntimeOnly(libs.junitPlatformLauncher)
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
}
