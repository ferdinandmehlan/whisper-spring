plugins {
    id("java-library")
}

java {
    toolchain {
        languageVersion.set(rootProject.extra["javaVersion"] as JavaLanguageVersion)
    }
}

dependencies {
    api("org.springframework.boot:spring-boot-starter-test:4.0.0")
    api("org.springframework.boot:spring-boot-starter-web:4.0.0")
    api("de.cronn:validation-file-assertions:0.8.0")
    api("com.fasterxml.jackson.core:jackson-databind:2.15.2")
}
