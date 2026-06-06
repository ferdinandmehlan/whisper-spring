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
    api(platform("org.springframework.boot:spring-boot-dependencies:4.0.6"))
    api(platform("org.springframework.ai:spring-ai-bom:1.1.7"))
    api("org.springframework.boot:spring-boot-starter")
    api("org.springframework.ai:spring-ai-model")

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
    dependsOn(":downloadTinyModel")
}

tasks.test {
    useJUnitPlatform()
}

tasks.javadoc {
    options {
        this as StandardJavadocDocletOptions
        addStringOption("Xdoclint:all,-missing", "-quiet")
    }
}
