plugins {
    id("java")
    id("org.springframework.boot") version "4.0.0"
}

java {
    toolchain {
        languageVersion.set(rootProject.extra["javaVersion"] as JavaLanguageVersion)
    }
}

dependencies {
    implementation(project(":whisper-spring"))
    implementation("info.picocli:picocli:4.7.7")

    annotationProcessor("info.picocli:picocli-codegen:4.7.7")

    testImplementation(project(":whisper-spring-test-common"))

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
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
}

tasks.test {
    useJUnitPlatform()
    dependsOn(":downloadTinyModel")
}
