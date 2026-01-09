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
