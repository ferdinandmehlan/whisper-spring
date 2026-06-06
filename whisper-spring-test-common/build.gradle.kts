plugins {
    id("java-library")
}

java {
    toolchain {
        languageVersion.set(rootProject.extra["javaVersion"] as JavaLanguageVersion)
    }
}

dependencies {
    api(platform("org.springframework.boot:spring-boot-dependencies:4.0.6"))
    api("org.springframework.boot:spring-boot-starter-test")
    api("org.springframework.boot:spring-boot-starter-web")
    api("de.cronn:validation-file-assertions:0.8.0")
    api("com.fasterxml.jackson.core:jackson-databind:2.15.2")
}
