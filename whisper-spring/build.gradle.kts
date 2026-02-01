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

dependencies {
    api("org.springframework.boot:spring-boot-starter:4.0.0")

    testImplementation(project(":whisper-spring-test-common"))

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
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

tasks.javadoc {
    options {
        this as StandardJavadocDocletOptions
        addStringOption("Xdoclint:all,-missing", "-quiet")
    }
}
