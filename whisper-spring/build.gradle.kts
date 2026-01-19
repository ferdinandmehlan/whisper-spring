plugins {
    id("java-library")
    id("maven-publish")
}

java {
    toolchain {
        languageVersion.set(rootProject.extra["javaVersion"] as JavaLanguageVersion)
    }

    withSourcesJar()
    withJavadocJar()
}

/*
 * Adding whisper.cpp java bindings as source set from git submodule
 * because the publishing maven job of the project is failing:
 * https://github.com/ggml-org/whisper.cpp/issues/3079
 * This is a workaround instead of adding as dependency.
 */
sourceSets {
    main {
        java {
            srcDirs("../whisper.cpp/bindings/java/src/main/java")
        }
    }
}

dependencies {
    api("org.springframework.boot:spring-boot-starter:4.0.0")
    api("net.java.dev.jna:jna:5.14.0")
    api("net.java.dev.jna:jna-platform:5.14.0")

    testImplementation(project(":whisper-spring-test-common"))

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

/*
 * Compile
 */

tasks.register<Copy>("copyWhisperNativeLibs") {
    from("$rootDir/libs")
    into(layout.buildDirectory.dir("resources/main/native"))
}

tasks.named("compileJava") {
    dependsOn("processResources", "copyWhisperNativeLibs")
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
